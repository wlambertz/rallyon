import type { Meta, StoryObj } from '@storybook/angular'

import { DashboardComponent } from '../../app/features/dashboard/dashboard.component'
import { pencilArtifactNote } from '../pencil-registry'

const meta = {
  title: 'Screens/Dashboard',
  component: DashboardComponent,
  tags: ['autodocs'],
  parameters: {
    layout: 'fullscreen',
    docs: {
      description: {
        component: [
          'Dashboard is the main operations-board screen for the organizer flow. Keep this story aligned with the committed Pencil screen file as the layout evolves.',
          pencilArtifactNote('screens/dashboard'),
        ].join('\n\n'),
      },
    },
  },
} satisfies Meta<DashboardComponent>

export default meta

type Story = StoryObj<DashboardComponent>

export const Default: Story = {}
