import { defineConfig } from 'cypress'

export default defineConfig({
  videosFolder: 'cypress/videos',
  screenshotsFolder: 'cypress/screenshots',
  fixturesFolder: 'cypress/fixtures',
  video: false,
  e2e: {
    setupNodeEvents(on, config) {
      return require('./cypress/plugins/index.ts').default(on, config)
    },
    specPattern: [
      'cypress/e2e/**/*.cy.{js,ts}',
      'src/**/*.integration.cy.{js,ts}',
      'src/**/*.e2e.cy.{js,ts}'
    ],
    baseUrl: 'http://localhost:59652'
  },
})
