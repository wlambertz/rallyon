import React from 'react';

/**
 * RallyOn Dialog — modal overlay. Scrim + centered panel with mono eyebrow,
 * hard seam, and a footer action row.
 */
export function Dialog({ open, title, eyebrow, onClose, footer = null, width = 460, children }) {
  React.useEffect(() => {
    const onKey = (e) => { if (e.key === 'Escape' && onClose) onClose(); };
    if (open) window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [open, onClose]);

  if (!open) return null;

  return (
    <div
      onMouseDown={(e) => { if (e.target === e.currentTarget && onClose) onClose(); }}
      style={{
        position: 'fixed', inset: 0, zIndex: 1000,
        background: 'var(--scrim)',
        display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 24,
        animation: 'ro-dialog-in var(--dur-2) var(--ease-standard)',
      }}
    >
      <style>{`@keyframes ro-dialog-in{from{opacity:0}to{opacity:1}}`}</style>
      <div style={{
        width, maxWidth: '100%', maxHeight: '90vh', display: 'flex', flexDirection: 'column',
        background: 'var(--paper-2)',
        border: 'var(--bw-heavy) solid var(--line-strong)',
        borderRadius: 'var(--r-2)',
        boxShadow: 'var(--shadow-pop)',
        overflow: 'hidden',
      }}>
        <header style={{
          display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: 12,
          padding: '14px 16px', borderBottom: 'var(--bw) solid var(--line-strong)',
        }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
            {eyebrow && <span style={{ font: '600 10px/1 var(--font-mono)', letterSpacing: 'var(--tracking-caps)', textTransform: 'uppercase', color: 'var(--ink-2)' }}>{eyebrow}</span>}
            {title && <span style={{ font: '600 18px/1.2 var(--font-display)', letterSpacing: '-0.01em', color: 'var(--ink-0)' }}>{title}</span>}
          </div>
          <button type="button" onClick={onClose} aria-label="Schließen" style={{
            border: '1px solid var(--line)', background: 'var(--paper-1)', cursor: 'pointer',
            width: 28, height: 28, borderRadius: 'var(--r-1)', color: 'var(--ink-0)',
            font: '700 14px/1 var(--font-mono)', flex: '0 0 28px',
          }}>×</button>
        </header>
        <div style={{ padding: 16, overflowY: 'auto', font: 'var(--fw-regular) 14px/1.55 var(--font-sans)', color: 'var(--ink-1)' }}>{children}</div>
        {footer && (
          <footer style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, padding: '12px 16px', borderTop: '1px solid var(--line)', background: 'var(--paper-1)' }}>{footer}</footer>
        )}
      </div>
    </div>
  );
}
