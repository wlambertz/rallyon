import type { Meta, StoryObj } from '@storybook/angular'

import { SettingsComponent } from '../../app/features/settings/settings.component'
import { pencilArtifactNote } from '../pencil-registry'

const meta = {
  title: 'Screens/Settings',
  component: SettingsComponent,
  tags: ['autodocs'],
  parameters: {
    layout: 'fullscreen',
    docs: {
      description: {
        component: [
          'Settings is the controls-register placeholder for organizer administration.',
          pencilArtifactNote('screens/settings'),
        ].join('\n\n'),
      },
    },
  },
} satisfies Meta<SettingsComponent>

export default meta

type Story = StoryObj<SettingsComponent>

export const Default: Story = {}
