import { Component, Input } from '@angular/core'
import type { Meta, StoryObj } from '@storybook/angular'
import { TagModule } from 'primeng/tag'

import { pencilArtifactNote } from '../pencil-registry'

type StatusTone = 'live' | 'draft' | 'ready' | 'warning'

@Component({
  selector: 'story-status-tag',
  standalone: true,
  imports: [TagModule],
  template: `
    <div class="story-stage">
      <p-tag [value]="label" [severity]="severity"></p-tag>
      <p class="story-note">
        Use <code>status</code> as the shared variant name in Pencil, Storybook, and future
        extracted Angular components.
      </p>
    </div>
  `,
  styles: [
    `
      .story-stage {
        display: grid;
        gap: 1rem;
        padding: 2rem;
        background: var(--rallyon-surface);
      }

      .story-note {
        margin: 0;
        max-width: 32rem;
        color: var(--rallyon-text);
      }

      :host ::ng-deep .p-tag {
        text-transform: uppercase;
        letter-spacing: 0.08em;
      }
    `,
  ],
})
class OrganizerStatusTagStoryComponent {
  @Input() status: StatusTone = 'live'

  get label(): string {
    switch (this.status) {
      case 'draft':
        return 'Draft'
      case 'ready':
        return 'Ready'
      case 'warning':
        return 'Attention'
      default:
        return 'Live'
    }
  }

  get severity(): 'contrast' | 'info' | 'success' | 'warn' {
    switch (this.status) {
      case 'draft':
        return 'contrast'
      case 'ready':
        return 'success'
      case 'warning':
        return 'warn'
      default:
        return 'info'
    }
  }
}

const meta = {
  title: 'Primitives/Status Tag',
  component: OrganizerStatusTagStoryComponent,
  tags: ['autodocs'],
  args: {
    status: 'live',
  },
  argTypes: {
    status: {
      control: 'inline-radio',
      options: ['live', 'draft', 'ready', 'warning'],
    },
  },
  parameters: {
    layout: 'padded',
    docs: {
      description: {
        component: [
          'Status tags are a small but important contract between the Pencil design system and the organizer UI. Keep the names and states aligned with the dashboard and navigation surfaces.',
          pencilArtifactNote('components/status-tag'),
        ].join('\n\n'),
      },
    },
  },
} satisfies Meta<OrganizerStatusTagStoryComponent>

export default meta

type Story = StoryObj<OrganizerStatusTagStoryComponent>

export const Playground: Story = {}
