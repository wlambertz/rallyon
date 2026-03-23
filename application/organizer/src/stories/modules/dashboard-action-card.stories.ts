import { Component, Input } from '@angular/core'
import type { Meta, StoryObj } from '@storybook/angular'

import { pencilArtifactNote } from '../pencil-registry'

type CardTone = 'default' | 'signal'
type CardDensity = 'comfortable' | 'compact'

@Component({
  selector: 'story-dashboard-action-card',
  standalone: true,
  template: `
    <article class="action-card" [class.action-card--signal]="tone === 'signal'" [class.action-card--compact]="density === 'compact'">
      <div class="action-card__icon">
        <span class="pi pi-plus-circle"></span>
      </div>
      <div class="action-card__content">
        <p class="action-card__eyebrow">Quick action</p>
        <h3>{{ heading }}</h3>
        <p>{{ description }}</p>
        <span class="action-card__link">Open module</span>
      </div>
    </article>
  `,
  styles: [
    `
      .action-card {
        display: grid;
        grid-template-columns: auto 1fr;
        gap: 1rem;
        max-width: 28rem;
        padding: 1.25rem;
        border: 1px solid rgba(15, 23, 42, 0.12);
        border-radius: 1rem;
        background: #ffffff;
        box-shadow: 0 12px 24px rgba(15, 23, 42, 0.06);
      }

      .action-card--signal {
        border-color: color-mix(in srgb, var(--rallyon-primary) 40%, white);
      }

      .action-card--compact {
        padding: 1rem;
      }

      .action-card__icon {
        display: grid;
        place-items: center;
        width: 3rem;
        height: 3rem;
        border-radius: 999px;
        background: color-mix(in srgb, var(--rallyon-primary) 10%, white);
        color: var(--rallyon-primary);
      }

      .action-card__content {
        display: grid;
        gap: 0.45rem;
      }

      .action-card__eyebrow,
      .action-card__link {
        margin: 0;
        font-size: 0.78rem;
        font-weight: 600;
        letter-spacing: 0.08em;
        text-transform: uppercase;
      }

      .action-card h3,
      .action-card p {
        margin: 0;
      }

      .action-card__link {
        color: var(--rallyon-primary);
      }
    `,
  ],
})
class DashboardActionCardStoryComponent {
  @Input() heading = 'Create event'
  @Input() description = 'Spin up a fresh bracket to test onboarding copy and flows.'
  @Input() tone: CardTone = 'default'
  @Input() density: CardDensity = 'comfortable'
}

const meta = {
  title: 'Modules/Dashboard Action Card',
  component: DashboardActionCardStoryComponent,
  tags: ['autodocs'],
  args: {
    heading: 'Create event',
    description: 'Spin up a fresh bracket to test onboarding copy and flows.',
    tone: 'default',
    density: 'comfortable',
  },
  argTypes: {
    tone: {
      control: 'inline-radio',
      options: ['default', 'signal'],
    },
    density: {
      control: 'inline-radio',
      options: ['comfortable', 'compact'],
    },
  },
  parameters: {
    layout: 'centered',
    docs: {
      description: {
        component: [
          'Pilot module for the Pencil linked-handoff workflow. Keep the local Pencil library asset and this Storybook story aligned before implementation changes land.',
          pencilArtifactNote('modules/dashboard-action-card'),
        ].join('\n\n'),
      },
    },
  },
} satisfies Meta<DashboardActionCardStoryComponent>

export default meta

type Story = StoryObj<DashboardActionCardStoryComponent>

export const Playground: Story = {}
