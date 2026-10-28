import React from 'react';

/**
 * RallyOn IconButton — square, icon-only control for toolbars and rows.
 */
export function IconButton({
  variant = 'secondary',
  size = 'md',
  disabled = false,
  active = false,
  title,
  onClick,
  children,
  style,
  ...rest
}) {
  const [hover, setHover] = React.useState(false);
  const dims = { sm: 30, md: 38, lg: 44 };
  const d = dims[size];

  const palettes = {
    primary: { bg: 'var(--accent)', fg: 'var(--accent-on)', bd: 'var(--line-strong)', hbg: 'var(--accent-hover)' },
    secondary: { bg: 'var(--paper-2)', fg: 'var(--ink-0)', bd: 'var(--line-strong)', hbg: 'var(--paper-0)' },
    ghost: { bg: 'transparent', fg: 'var(--ink-1)', bd: 'transparent', hbg: 'var(--paper-0)' },
  };
  const p = palettes[variant] || palettes.secondary;
  const on = active || hover;

  return (
    <button
      type="button"
      title={title}
      aria-label={title}
      disabled={disabled}
      onClick={onClick}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      style={{
        width: d, height: d, flex: `0 0 ${d}px`,
        display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
        background: active ? 'var(--accent-soft)' : (on ? p.hbg : p.bg),
        color: active ? 'var(--accent)' : p.fg,
        border: `var(--bw) solid ${active ? 'var(--accent)' : p.bd}`,
        borderRadius: 'var(--r-1)',
        cursor: disabled ? 'not-allowed' : 'pointer',
        opacity: disabled ? 0.4 : 1,
        transition: 'var(--transition-ui)',
        ...style,
      }}
      {...rest}
    >
      {children}
    </button>
  );
}
