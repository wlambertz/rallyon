import type { Meta, StoryObj } from '@storybook/angular'

import { LoginComponent } from '../../app/features/auth/login.component'
import { pencilArtifactNote } from '../pencil-registry'

const meta = {
  title: 'Screens/Login',
  component: LoginComponent,
  tags: ['autodocs'],
  parameters: {
    layout: 'fullscreen',
    docs: {
      description: {
        component: [
          'Use this story as the implementation checkpoint for the organizer sign-in surface.',
          pencilArtifactNote('screens/login'),
        ].join('\n\n'),
      },
    },
  },
} satisfies Meta<LoginComponent>

export default meta

type Story = StoryObj<LoginComponent>

export const Default: Story = {}
