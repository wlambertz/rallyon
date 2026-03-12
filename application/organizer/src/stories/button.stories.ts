import { CommonModule } from '@angular/common'
import { moduleMetadata, type Meta, type StoryObj } from '@storybook/angular'
import { fn } from 'storybook/test'
import { ButtonModule } from 'primeng/button'

type ButtonSeverity =
  | 'primary'
  | 'secondary'
  | 'success'
  | 'info'
  | 'warn'
  | 'help'
  | 'danger'
  | 'contrast'

const BUTTON_SEVERITIES: ButtonSeverity[] = [
  'primary',
  'secondary',
  'success',
  'info',
  'warn',
  'help',
  'danger',
  'contrast',
]

interface ButtonStoryArgs {
  label: string
  severity: ButtonSeverity
  size?: 'small' | 'large'
  rounded: boolean
  outlined: boolean
  text: boolean
  raised: boolean
  link: boolean
  loading: boolean
  variants?: ButtonSeverity[]
  onClick: (event: Event) => void
}

// More on how to set up stories at: https://storybook.js.org/docs/writing-stories
const meta: Meta<ButtonStoryArgs> = {
  title: 'Components/Button',
  tags: ['autodocs'],
  decorators: [
    moduleMetadata({
      imports: [CommonModule, ButtonModule],
    }),
  ],
  argTypes: {
    severity: {
      control: 'select',
      options: BUTTON_SEVERITIES,
    },
    size: {
      control: 'select',
      options: ['small', 'large'],
    },
    variants: {
      control: 'check',
      options: BUTTON_SEVERITIES,
    },
  },
  args: {
    label: 'Action',
    severity: 'primary',
    rounded: false,
    outlined: false,
    text: false,
    raised: false,
    link: false,
    loading: false,
    onClick: fn(),
  },
}

export default meta
type Story = StoryObj<ButtonStoryArgs>

const renderButton = (args: ButtonStoryArgs) => ({
  props: args,
  template: `
    <p-button
      [label]="label"
      [severity]="severity"
      [size]="size"
      [rounded]="rounded"
      [outlined]="outlined"
      [text]="text"
      [link]="link"
      [raised]="raised"
      [loading]="loading"
      (onClick)="onClick($event)"
    ></p-button>
  `,
})

// More on writing stories with args: https://storybook.js.org/docs/writing-stories/args
export const Primary: Story = {
  render: renderButton,
}

export const Secondary: Story = {
  render: renderButton,
  args: {
    severity: 'secondary',
  },
}

export const Success: Story = {
  render: renderButton,
  args: {
    severity: 'success',
  },
}

export const Rounded: Story = {
  render: renderButton,
  args: {
    rounded: true,
  },
}

export const Text: Story = {
  render: renderButton,
  args: {
    text: true,
    severity: 'secondary',
  },
}

export const Link: Story = {
  render: renderButton,
  args: {
    link: true,
  },
}

export const Large: Story = {
  render: renderButton,
  args: {
    size: 'large',
  },
}

export const AllSeverities: Story = {
  args: {
    variants: ['primary', 'secondary', 'success', 'info', 'danger', 'help', 'warn', 'contrast'],
  },
  render: (args) => ({
    props: {
      ...args,
      variants: args.variants && args.variants.length > 0 ? args.variants : BUTTON_SEVERITIES,
    },
    template: `
      <div class="flex flex-wrap gap-3">
        <p-button
          *ngFor="let kind of variants"
          [label]="kind"
          [severity]="kind"
          [size]="size"
          [rounded]="rounded"
          [outlined]="outlined"
          [text]="text"
          [link]="link"
          [raised]="raised"
          [loading]="loading"
          (onClick)="onClick($event)"
        ></p-button>
      </div>
    `,
  }),
}
