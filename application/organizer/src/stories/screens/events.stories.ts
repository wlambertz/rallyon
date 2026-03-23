import type { Meta, StoryObj } from '@storybook/angular'

import { EventsComponent } from '../../app/features/events/events.component'
import { pencilArtifactNote } from '../pencil-registry'

const meta = {
  title: 'Screens/Events',
  component: EventsComponent,
  tags: ['autodocs'],
  parameters: {
    layout: 'fullscreen',
    docs: {
      description: {
        component: [
          'Events is the first structured workbench stub in the organizer flow. Use it to keep placeholder-state designs aligned before the real event workspace is built.',
          pencilArtifactNote('screens/events'),
        ].join('\n\n'),
      },
    },
  },
} satisfies Meta<EventsComponent>

export default meta

type Story = StoryObj<EventsComponent>

export const Default: Story = {}
