import { applicationConfig } from '@storybook/angular'
import type { Preview } from '@storybook/angular'

import { appConfig } from '../src/app/app.config'

const preview: Preview = {
  decorators: [applicationConfig(appConfig)],
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
  },
}

export default preview
