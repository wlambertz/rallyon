import { Component } from '@angular/core'
import type { Meta, StoryObj } from '@storybook/angular'

import { pencilArtifactNote } from '../pencil-registry'

@Component({
  selector: 'story-pencil-workflow',
  standalone: true,
  template: `
    <article class="workflow">
      <section>
        <h1>Pencil Workflow</h1>
        <p>
          RallyOn uses a repo-backed Pencil.dev workflow for new organizer UI work: keep <code>.pen</code>
          files in the workspace, keep Storybook as the code-side catalog, and sync visual changes
          back into Angular tokens and components before merge.
        </p>
      </section>

      <section>
        <h2>Workspace layout</h2>
        <ul>
          <li><strong>application/organizer/design/pencil/organizer-ui.lib.pen</strong> for reusable components and variables</li>
          <li><strong>application/organizer/design/pencil/screens/</strong> for screen-level organizer flows</li>
          <li><strong>src/stories/pencil-registry.ts</strong> as the local bridge between Storybook and committed Pencil assets</li>
        </ul>
      </section>

      <section>
        <h2>Token sync contract</h2>
        <table>
          <thead>
            <tr>
              <th>Pencil surface</th>
              <th>Code source</th>
              <th>Intent</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><code>variables</code></td>
              <td><code>src/styles/settings/_tokens.scss</code></td>
              <td>Keep colors, spacing, and motion tokens aligned.</td>
            </tr>
            <tr>
              <td><code>text styles</code></td>
              <td><code>src/styles/elements/_typography.scss</code> and <code>src/index.html</code></td>
              <td>Keep display, body, label, and signal-counter roles consistent.</td>
            </tr>
            <tr>
              <td><code>semantic states</code></td>
              <td><code>src/app/rallyonpreset.ts</code></td>
              <td>Mirror PrimeNG status intent and button semantics.</td>
            </tr>
          </tbody>
        </table>
      </section>

      <section>
        <h2>Working loop</h2>
        <ol>
          <li>Open the relevant <code>.pen</code> file in the organizer workspace.</li>
          <li>Import or refresh the library file if you need shared components.</li>
          <li>Design or revise the screen/module in Pencil.</li>
          <li>Mirror the same surface in Storybook and update Angular code.</li>
          <li>Run a drift review so the <code>.pen</code> file, story, and code still describe the same UI.</li>
        </ol>
      </section>

      <section>
        <h2>MCP verification</h2>
        <p>
          Pencil runs its MCP server locally. When Pencil is open, run <code>/mcp</code> in Codex
          and confirm Pencil appears in the server list before using design-edit prompts.
        </p>
      </section>
    </article>
  `,
  styles: [
    `
      .workflow {
        display: grid;
        gap: 2rem;
        padding: 2rem;
        max-width: 72rem;
        margin: 0 auto;
        color: var(--rallyon-text);
      }

      .workflow section {
        display: grid;
        gap: 0.85rem;
      }

      .workflow h1,
      .workflow h2,
      .workflow p,
      .workflow ul,
      .workflow ol {
        margin: 0;
      }

      .workflow ul,
      .workflow ol {
        padding-left: 1.25rem;
      }

      .workflow table {
        width: 100%;
        border-collapse: collapse;
        background: #ffffff;
      }

      .workflow th,
      .workflow td {
        padding: 0.85rem;
        border: 1px solid rgba(15, 23, 42, 0.12);
        text-align: left;
        vertical-align: top;
      }
    `,
  ],
})
class PencilWorkflowStoryComponent {}

const meta = {
  title: 'Foundations/Pencil Workflow',
  component: PencilWorkflowStoryComponent,
  tags: ['autodocs'],
  parameters: {
    layout: 'fullscreen',
    docs: {
      description: {
        component: [
          'Living workflow reference for using Pencil.dev and Storybook together in the organizer app.',
          pencilArtifactNote('foundations/workflow'),
        ].join('\n\n'),
      },
    },
  },
} satisfies Meta<PencilWorkflowStoryComponent>

export default meta

type Story = StoryObj<PencilWorkflowStoryComponent>

export const Default: Story = {}
