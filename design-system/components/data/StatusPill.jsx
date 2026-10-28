import React from 'react';

const TONES = {
  live:   { fg: 'var(--status-live)',   bg: 'var(--status-live-soft)',   dot: 'var(--status-live)' },
  ok:     { fg: 'var(--status-ok)',     bg: 'var(--status-ok-soft)',     dot: 'var(--status-ok)' },
  warn:   { fg: 'var(--status-warn)',   bg: 'var(--status-warn-soft)',   dot: 'var(--status-warn)' },
  danger: { fg: 'var(--status-danger)', bg: 'var(--status-danger-soft)', dot: 'var(--status-danger)' },
  idle:   { fg: 'var(--ink-1)',         bg: 'var(--status-idle-soft)',   dot: 'var(--status-idle)' },
};

/**
 * RallyOn StatusPill — operational status chip. Color + dot + UPPERCASE label.
 * `live` blinks (mechanical 2-step).
 */
export function StatusPill({ tone = 'idle', label, dot = true, solid = false, style, ...rest }) {
  const t = TONES[tone] || TONES.idle;
  const blink = tone === 'live';

  const solidStyle = {
    background: t.fg, color: tone === 'live' || tone === 'warn' ? '#fff' : '#fff',
    borderColor: 'transparent',
  };

  return (
    <span
      style={{
        display: 'inline-flex', alignItems: 'center', gap: 6,
        height: 22, padding: '0 10px',
        borderRadius: 'var(--r-pill)',
        border: `var(--bw) solid ${solid ? 'transparent' : t.fg}`,
        background: solid ? t.fg : t.bg,
        color: solid ? '#fff' : t.fg,
        font: '600 10px/1 var(--font-mono)',
        letterSpacing: 'var(--tracking-caps)', textTransform: 'uppercase',
        whiteSpace: 'nowrap',
        ...style,
      }}
      {...rest}
    >
      {dot && (
        <span style={{
          width: 7, height: 7, borderRadius: '50%',
          background: solid ? '#fff' : t.dot,
          animation: blink ? 'ro-blink 1s steps(1) infinite' : 'none',
        }} />
      )}
      {label}
    </span>
  );
}
