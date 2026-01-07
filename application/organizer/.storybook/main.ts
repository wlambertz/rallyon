import type { StorybookConfig } from '@storybook/angular'
import path from 'node:path'

const config: StorybookConfig = {
  stories: ['../src/**/*.mdx', '../src/**/*.stories.@(js|jsx|mjs|ts|tsx)'],
  addons: ['@storybook/addon-docs', '@storybook/addon-onboarding'],
  framework: {
    name: '@storybook/angular',
    options: {},
  },
  core: {
    builder: '@storybook/builder-webpack5',
  },
  docs: {
    autodocs: 'tag',
  },
  angularBuilderOptions: {
    styles: ['src/styles.scss'],
  },
  webpackFinal: async (baseConfig) => {
    const webpackModule = await import('webpack')
    const DefinePlugin =
      webpackModule.DefinePlugin ??
      (webpackModule.default && // fallback for CommonJS default export interop
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        (webpackModule.default as any).DefinePlugin)

    if (!DefinePlugin) {
      return baseConfig
    }
    const defineKey = 'process.env.NODE_ENV'

    baseConfig.plugins = baseConfig.plugins?.filter((plugin) => {
      if (plugin instanceof DefinePlugin) {
        const definitions = (plugin as { definitions?: Record<string, unknown> }).definitions
        return !(definitions && defineKey in definitions)
      }
      return true
    })

    baseConfig.plugins?.push(
      new DefinePlugin({
        [defineKey]: JSON.stringify(process.env.NODE_ENV ?? 'development'),
      })
    )

    baseConfig.cache = {
      type: 'filesystem',
      cacheDirectory: path.resolve(process.env.STORYBOOK_CACHE_DIR ?? '/tmp/storybook-cache'),
      name: 'dev-server',
    }

    // Enable polling for file watching (fixes WSL cross-filesystem issues)
    baseConfig.watchOptions = {
      poll: 2000, // Check for changes every 2 seconds
      aggregateTimeout: 300,
      ignored: /node_modules/,
    }

    return baseConfig
  },
}
export default config
