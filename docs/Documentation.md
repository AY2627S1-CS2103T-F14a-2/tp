---
  layout: default.md
  title: "Documentation guide"
  pageNav: 3
---

# Documentation Guide

* We use [**MarkBind**](https://markbind.org/) to manage documentation.
* The `docs/` folder contains the source files for the documentation website.
* To learn how set it up and maintain the project website, follow the guide [[se-edu/guides] Working with Forked MarkBind sites](https://se-education.org/guides/tutorials/markbind-forked-sites.html) for project documentation.

## Local preview and diagram updates

Use Node.js 22.12 or later, Java and Graphviz. From `docs/`, run `npm ci` once after pulling dependency changes,
then `npm run serve` to preview the site. Both this installation and GitHub deployment use MarkBind 7.2.1,
so the bundled PlantUML renderer is consistent (see [course forum #722](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/722)).
Use `npm ls markbind-cli` to check the installed version.

GitHub and VS Code's standard Markdown previews do not render MarkBind's `<puml>` components. The DG therefore
also includes ordinary `<img>` fallbacks from `images/diagrams/`. The website hides these fallbacks and renders
the adjacent `<puml>` components, including during live preview.

After editing a diagram in `diagrams/`, run `npm run build`. The build generates the website and then refreshes
the PNG fallbacks from the generated diagrams. Commit the updated PNGs together with the `.puml` sources.
When adding a DG diagram, follow an existing `<puml>` / `<img class="diagram-preview-fallback">` pair, using the
same diagram filename, width and alternative text. Keep the `.puml` file as the editable source.

**Style guidance:**

* Follow the [**_Google developer documentation style guide_**](https://developers.google.com/style).
* Also relevant is the [_se-edu/guides **Markdown coding standard**_](https://se-education.org/guides/conventions/markdown.html).


**Converting to PDF**

* See the guide [_se-edu/guides **Saving web documents as PDF files**_](https://se-education.org/guides/tutorials/savingPdf.html).
