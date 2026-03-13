import { defineConfig } from '@playwright/test'

const executablePath =
  process.env.PLAYWRIGHT_CHROME_BIN || (process.env.CI ? '/usr/bin/chromium-browser' : undefined)

export default defineConfig({
  testDir: './tests',
  timeout: 60_000,
  expect: {
    timeout: 5_000,
  },
  use: {
    baseURL: 'http://localhost:4200',
    headless: true,
    executablePath,
    trace: 'on-first-retry',
  },
  webServer: {
    command: 'npm start',
    port: 4200,
    reuseExistingServer: !process.env.CI,
    stdout: 'pipe',
    stderr: 'pipe',
  },
})
