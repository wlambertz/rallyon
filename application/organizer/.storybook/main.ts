import type { StorybookConfig } from '@storybook/angular'

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
      }),
    )

    baseConfig.watchOptions = {
      ...(baseConfig.watchOptions ?? {}),
      poll: 1500,
      aggregateTimeout: 300,
      ignored: ['**/node_modules/**', '**/.angular/**', '**/.cache/**'],
    }

    return baseConfig
  },
}
export default config
