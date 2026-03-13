import { test, expect } from '@playwright/test'

const credentials = {
  username: 'organizer',
  password: 'rallyon',
}

test.describe('Organizer login flow', () => {
  test('signs in and reaches the dashboard', async ({ page }) => {
    await page.goto('/')

    await expect(page.getByText('Credentials for this demo: organizer / rallyon.')).toBeVisible()
    await expect(page.getByRole('button', { name: 'Sign in' })).toBeVisible()

    await page.getByLabel('Username').fill(credentials.username)
    await page.getByLabel('Password').fill(credentials.password)
    await page.getByRole('button', { name: 'Sign in' }).click()

    await expect(page.getByRole('heading', { name: 'Welcome back!' })).toBeVisible()
    await expect(page.getByText('Quick actions')).toBeVisible()
  })
})
