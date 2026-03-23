import { computed, signal } from '@angular/core'
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular'

import { LayoutService } from '../app/core/services/layout.service'
import { NavigationComponent } from '../app/layout/navigation/navigation.component'
import { pencilArtifactNote } from './pencil-registry'

class MockLayoutService {
  private readonly sidebarState = signal(true)

  readonly sidebarVisible = computed(() => this.sidebarState())

  openSidebar(): void {
    this.sidebarState.set(true)
  }

  closeSidebar(): void {
    this.sidebarState.set(false)
  }

  toggleSidebar(): void {
    this.sidebarState.update((visible) => !visible)
  }
}

const meta: Meta<NavigationComponent> = {
  title: 'Layout/Navigation',
  component: NavigationComponent,
  tags: ['autodocs'],
  decorators: [
    applicationConfig({
      providers: [{ provide: LayoutService, useClass: MockLayoutService }],
    }),
  ],
  parameters: {
    layout: 'fullscreen',
    docs: {
      description: {
        component: [
          'Navigation is the base layout surface for the organizer shell. Use the local Pencil screen file as the design reference instead of a remote design link.',
          pencilArtifactNote('layout/navigation'),
        ].join('\n\n'),
      },
    },
  },
}

export default meta
type Story = StoryObj<NavigationComponent>

export const Default: Story = {}
