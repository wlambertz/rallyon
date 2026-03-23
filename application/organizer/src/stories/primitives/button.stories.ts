import { Component, Input } from '@angular/core'
import type { Meta, StoryObj } from '@storybook/angular'
import { ButtonModule } from 'primeng/button'

import { pencilArtifactNote } from '../pencil-registry'

type ButtonTone = 'primary' | 'secondary' | 'text'
type ButtonSize = 'compact' | 'default'

@Component({
  selector: 'story-organizer-button',
  standalone: true,
  imports: [ButtonModule],
  template: `
    <div class="story-stage">
      <p-button
        [label]="label"
        [icon]="icon ? 'pi pi-plus-circle' : undefined"
        [styleClass]="buttonClass"
      />
      <p class="story-note">
        Variant names mirror Pencil components, Storybook controls, and Angular code so design and
        code talk about the same thing.
      </p>
      <p class="story-note">
        Use <code>tone</code>, <code>size</code>, and <code>icon</code> as the shared property
        names across all three surfaces.
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
        max-width: 36rem;
        color: var(--rallyon-text);
      }

      :host ::ng-deep .p-button.story-button--secondary {
        background: transparent;
        color: var(--rallyon-text);
      }

      :host ::ng-deep .p-button.story-button--text {
        background: transparent;
        border-color: transparent;
        color: var(--rallyon-primary);
        box-shadow: none;
      }

      :host ::ng-deep .p-button.story-button--compact {
        padding-block: 0.55rem;
        padding-inline: 0.85rem;
        font-size: 0.9rem;
      }
    `,
  ],
})
class OrganizerButtonStoryComponent {
  @Input() label = 'Create event'
  @Input() tone: ButtonTone = 'primary'
  @Input() size: ButtonSize = 'default'
  @Input() icon = true

  get buttonClass(): string {
    const classes = [`story-button--${this.tone}`]

    if (this.tone === 'secondary') {
      classes.push('p-button-outlined')
    }

    if (this.tone === 'text') {
      classes.push('p-button-text')
    }

    if (this.size === 'compact') {
      classes.push('story-button--compact')
    }

    return classes.join(' ')
  }
}

const meta = {
  title: 'Primitives/Button',
  component: OrganizerButtonStoryComponent,
  tags: ['autodocs'],
  args: {
    label: 'Create event',
    tone: 'primary',
    size: 'default',
    icon: true,
  },
  argTypes: {
    tone: {
      control: 'inline-radio',
      options: ['primary', 'secondary', 'text'],
    },
    size: {
      control: 'inline-radio',
      options: ['compact', 'default'],
    },
    icon: {
      control: 'boolean',
    },
  },
  parameters: {
    layout: 'padded',
    docs: {
      description: {
        component: [
          'First organizer primitive story. Keep the Pencil component and the Storybook controls aligned before extracting a dedicated Angular component.',
          pencilArtifactNote('components/button'),
        ].join('\n\n'),
      },
    },
  },
} satisfies Meta<OrganizerButtonStoryComponent>

export default meta

type Story = StoryObj<OrganizerButtonStoryComponent>

export const Playground: Story = {}

export const TextAction: Story = {
  args: {
    tone: 'text',
    label: 'Return to dashboard',
    icon: false,
  },
}
