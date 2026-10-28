import React from 'react';

const TONES = {
  info:   { fg: 'var(--ink-0)',         bg: 'var(--paper-2)',            bar: 'var(--ink-0)' },
  live:   { fg: 'var(--status-live)',   bg: 'var(--status-live-soft)',   bar: 'var(--status-live)' },
  ok:     { fg: 'var(--status-ok)',     bg: 'var(--status-ok-soft)',     bar: 'var(--status-ok)' },
  warn:   { fg: 'var(--status-warn)',   bg: 'var(--status-warn-soft)',   bar: 'var(--status-warn)' },
  danger: { fg: 'var(--status-danger)', bg: 'var(--status-danger-soft)', bar: 'var(--status-danger)' },
};

/**
 * RallyOn Banner — inline system message with a left status bar.
 */
export function Banner({ tone = 'info', title, children, action = null, onDismiss, style, ...rest }) {
  const t = TONES[tone] || TONES.info;
  return (
    <div
      role="status"
      style={{
        display: 'flex', alignItems: 'flex-start', gap: 12,
        padding: '11px 14px',
        background: t.bg,
        border: '1px solid var(--line)',
        borderLeft: `3px solid ${t.bar}`,
        borderRadius: 'var(--r-1)',
        ...style,
      }}
      {...rest}
    >
      <div style={{ flex: 1, minWidth: 0, display: 'flex', flexDirection: 'column', gap: 2 }}>
        {title && <span style={{ font: '600 13px/1.3 var(--font-sans)', color: 'var(--ink-0)' }}>{title}</span>}
        {children && <span style={{ font: '400 13px/1.45 var(--font-sans)', color: 'var(--ink-1)' }}>{children}</span>}
      </div>
      {action}
      {onDismiss && (
        <button type="button" onClick={onDismiss} aria-label="Ausblenden" style={{
          border: 'none', background: 'transparent', cursor: 'pointer', color: 'var(--ink-2)',
          font: '700 14px/1 var(--font-mono)', padding: 0, marginTop: -1,
        }}>×</button>
      )}
    </div>
  );
}
