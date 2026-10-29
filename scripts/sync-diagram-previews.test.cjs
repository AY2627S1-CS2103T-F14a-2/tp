/** Exercises the real exporter against isolated output folders, including failed PlantUML renders. */
const assert = require('node:assert/strict');
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const { spawnSync } = require('node:child_process');
const { test } = require('node:test');

const oldImage = fs.readFileSync(path.join(__dirname, '../images/diagrams/ArchitectureDiagram.png'));
const newImage = fs.readFileSync(path.join(__dirname, '../images/diagrams/ArchitectureSequenceDiagram.png'));

/** Copies the exporter into a disposable documentation tree with two existing previews. */
function createFixture(t, secondImage) {
  const root = fs.mkdtempSync(path.join(os.tmpdir(), 'spotter-diagram-test-'));
  t.after(() => {
    // Only remove the temporary folder created by this test.
    assert.equal(path.dirname(root), path.resolve(os.tmpdir()));
    assert.ok(path.basename(root).startsWith('spotter-diagram-test-'));
    fs.rmSync(root, { recursive: true, force: true });
  });
  const outputDirectories = ['images/diagrams', '_site/images/diagrams'];
  for (const directory of ['scripts', '_site/diagrams', ...outputDirectories]) {
    fs.mkdirSync(path.join(root, directory), { recursive: true });
  }
  const script = path.join(root, 'scripts/sync-diagram-previews.cjs');
  fs.copyFileSync(path.join(__dirname, 'sync-diagram-previews.cjs'), script);
  fs.writeFileSync(path.join(root, 'DeveloperGuide.md'),
    '<puml src="diagrams/First.puml" />\n<puml src="diagrams/Second.puml" />\n');
  fs.writeFileSync(path.join(root, '_site/diagrams/First.png'), newImage);
  if (secondImage !== undefined) {
    fs.writeFileSync(path.join(root, '_site/diagrams/Second.png'), secondImage);
  }
  const outputs = outputDirectories.flatMap(directory => ['First.png', 'Second.png']
    .map(filename => path.join(root, directory, filename)));
  outputs.forEach(filename => fs.writeFileSync(filename, oldImage));
  return {
    outputs,
    run: () => spawnSync(process.execPath, [script], { encoding: 'utf8', windowsHide: true })
  };
}

test('valid renders refresh committed and built previews', t => {
  const fixture = createFixture(t, newImage);
  const result = fixture.run();
  assert.equal(result.status, 0, result.stderr);
  fixture.outputs.forEach(filename => assert.deepEqual(fs.readFileSync(filename), newImage));
});

for (const [description, invalidImage] of [
  ['JVM crash text', Buffer.from('There is insufficient memory for the Java Runtime Environment to continue.\n')],
  ['an empty file', Buffer.alloc(0)],
  ['a truncated PNG signature', oldImage.subarray(0, 4)]
]) {
  test(`${description} in a later render leaves every existing preview unchanged`, t => {
    const fixture = createFixture(t, invalidImage);
    const result = fixture.run();
    assert.notEqual(result.status, 0);
    assert.match(result.stderr, /MarkBind did not generate a valid PNG: .*Second\.png/);
    fixture.outputs.forEach(filename => assert.deepEqual(fs.readFileSync(filename), oldImage));
  });
}

test('a missing later render leaves every existing preview unchanged', t => {
  const fixture = createFixture(t);
  const result = fixture.run();
  assert.notEqual(result.status, 0);
  assert.match(result.stderr, /ENOENT.*Second\.png/);
  fixture.outputs.forEach(filename => assert.deepEqual(fs.readFileSync(filename), oldImage));
});
