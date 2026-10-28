import React from 'react';

/**
 * RallyOn Card — a module: paper surface, hard seam, optional uppercase
 * mono eyebrow header with a hairline divider, optional header action.
 */
export function Card({
  eyebrow,
  title,
  action = null,
  raised = false,
  padded = true,
  accent = false,
  children,
  style,
  ...rest
}) {
  return (
    <section
      style={{
        background: 'var(--paper-2)',
        border: 'var(--bw) solid var(--line-strong)',
        borderRadius: 'var(--r-2)',
        boxShadow: raised ? 'var(--shadow-2)' : 'none',
        borderLeft: accent ? '3px solid var(--accent)' : 'var(--bw) solid var(--line-strong)',
        display: 'flex', flexDirection: 'column',
        overflow: 'hidden',
        ...style,
      }}
      {...rest}
    >
      {(eyebrow || title || action) && (
        <header style={{
          display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 12,
          padding: '12px 16px',
          borderBottom: '1px solid var(--line)',
        }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 3, minWidth: 0 }}>
            {eyebrow && (
              <span style={{ font: '600 10px/1 var(--font-mono)', letterSpacing: 'var(--tracking-caps)', textTransform: 'uppercase', color: 'var(--ink-2)' }}>{eyebrow}</span>
            )}
            {title && (
              <span style={{ font: '600 16px/1.2 var(--font-display)', letterSpacing: '-0.01em', color: 'var(--ink-0)' }}>{title}</span>
            )}
          </div>
          {action}
        </header>
      )}
      <div style={{ padding: padded ? 'var(--card-pad)' : 0, flex: 1 }}>{children}</div>
    </section>
  );
}
