import { Component } from '@angular/core'
import { CommonModule } from '@angular/common'
import { Panel } from 'primeng/panel'
import { DividerModule } from 'primeng/divider'
import { Button } from 'primeng/button'
import { RollingRibbon } from '../../shared/components/rolling-ribbon/rolling-ribbon'

@Component({
  selector: 'ro-sidenav',
  imports: [CommonModule, Panel, DividerModule, Button, RollingRibbon],
  templateUrl: './sidenav.html',
  styleUrl: './sidenav.scss',
})
export class Sidenav {
  sidebarExpanded = true

  systemMessages: string[] = ['Keep Calm and RallyOn!']

  toggleSidebar(): void {
    this.sidebarExpanded = !this.sidebarExpanded
  }
}
