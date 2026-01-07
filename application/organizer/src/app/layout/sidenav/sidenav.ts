import { Component } from '@angular/core'
import { CommonModule } from '@angular/common'
import { Panel } from 'primeng/panel'
import { DividerModule } from 'primeng/divider'
import { ButtonModule, ButtonDirective } from 'primeng/button'
import { MenuModule } from 'primeng/menu'
import { MenuItem } from 'primeng/api'
import { RollingRibbon } from '../../shared/components/rolling-ribbon/rolling-ribbon'

@Component({
  selector: 'ro-sidenav',
  imports: [CommonModule, Panel, DividerModule, ButtonModule, MenuModule, RollingRibbon, ButtonDirective],
  templateUrl: './sidenav.html',
  styleUrl: './sidenav.scss',
})
export class Sidenav {
  sidebarExpanded = true

  systemMessages: string[] = ['Keep Calm and RallyOn!']
  baseRoutes: MenuItem[] = [
    { label: 'Dashboard', url: '#' },
    { label: 'Tournaments', url: '#' },
    { label: 'Venues', url: '#' },
    { label: 'Officials', url: '#' },
    { label: 'Check-in', url: '#' },
    { label: 'Reports', url: '#' },
    { label: 'Settings', url: '#' },
    { label: 'Help', url: '#' },
  ]

  toggleSidebar(): void {
    this.sidebarExpanded = !this.sidebarExpanded
  }
}
