import React from 'react';

/**
 * RallyOn Button — primary operational action control.
 * Brutalist: hard ink seam, low radius, offset print-block on primary.
 */
export function Button({
  variant = 'primary',
  size = 'md',
  block = false,
  disabled = false,
  type = 'button',
  iconLeft = null,
  iconRight = null,
  onClick,
  children,
  style,
  ...rest
}) {
  const [state, setState] = React.useState('idle'); // idle | hover | active

  const heights = { sm: 'var(--control-sm)', md: 'var(--control-md)', lg: 'var(--control-lg)' };
  const pads = { sm: '0 12px', md: '0 16px', lg: '0 22px' };
  const fontSizes = { sm: '12px', md: '13px', lg: '15px' };

  const base = {
    display: block ? 'flex' : 'inline-flex',
    width: block ? '100%' : 'auto',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
    height: heights[size],
    padding: pads[size],
    font: `var(--fw-semibold) ${fontSizes[size]}/1 var(--font-sans)`,
    letterSpacing: '0.01em',
    borderRadius: 'var(--r-1)',
    border: 'var(--bw) solid var(--line-strong)',
    cursor: disabled ? 'not-allowed' : 'pointer',
    opacity: disabled ? 0.4 : 1,
    transition: 'transform var(--dur-1) var(--ease-standard), background var(--dur-1) var(--ease-standard)',
    userSelect: 'none',
    whiteSpace: 'nowrap',
  };

  const palettes = {
    primary: {
      idle:   { background: 'var(--ink-0)', color: 'var(--paper-1)' },
      hover:  { background: '#33302A', color: 'var(--paper-1)' },
      active: { background: '#000', color: 'var(--paper-1)', transform: 'translateY(1px)' },
    },
    secondary: {
      idle:   { background: 'var(--paper-2)', color: 'var(--ink-0)' },
      hover:  { background: 'var(--paper-0)', color: 'var(--ink-0)' },
      active: { background: 'var(--line-soft)', color: 'var(--ink-0)' },
    },
    ghost: {
      idle:   { background: 'transparent', color: 'var(--ink-0)', borderColor: 'transparent' },
      hover:  { background: 'var(--paper-0)', color: 'var(--ink-0)', borderColor: 'var(--line)' },
      active: { background: 'var(--line-soft)', color: 'var(--ink-0)', borderColor: 'var(--line)' },
    },
    danger: {
      idle:   { background: 'var(--status-danger)', color: '#fff' },
      hover:  { background: '#B12F1E', color: '#fff' },
      active: { background: '#9A2818', color: '#fff', transform: 'translateY(1px)' },
    },
  };

  const p = palettes[variant] || palettes.primary;
  const s = disabled ? p.idle : p[state];

  return (
    <button
      type={type}
      disabled={disabled}
      onClick={onClick}
      onMouseEnter={() => !disabled && setState('hover')}
      onMouseLeave={() => !disabled && setState('idle')}
      onMouseDown={() => !disabled && setState('active')}
      onMouseUp={() => !disabled && setState('hover')}
      style={{ ...base, ...s, ...style }}
      {...rest}
    >
      {iconLeft}
      {children}
      {iconRight}
    </button>
  );
}
