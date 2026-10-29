import React from 'react';

/**
 * RallyOn Tabs — underline-seam tab bar for switching views within a module.
 * tabs: [{ id, label, count }]
 */
export function Tabs({ tabs = [], value, defaultValue, onChange, style, ...rest }) {
  const isControlled = value !== undefined;
  const [internal, setInternal] = React.useState(defaultValue ?? (tabs[0] && tabs[0].id));
  const active = isControlled ? value : internal;

  const select = (id) => {
    if (!isControlled) setInternal(id);
    onChange && onChange(id);
  };

  return (
    <div
      role="tablist"
      style={{ display: 'flex', gap: 0, borderBottom: 'var(--bw) solid var(--line-strong)', ...style }}
      {...rest}
    >
      {tabs.map((t) => {
        const on = t.id === active;
        return (
          <button
            key={t.id}
            role="tab"
            aria-selected={on}
            onClick={() => select(t.id)}
            style={{
              position: 'relative',
              display: 'inline-flex', alignItems: 'center', gap: 7,
              padding: '9px 14px', marginBottom: -1.5,
              border: 'none', background: 'transparent', cursor: 'pointer',
              font: `${on ? 600 : 500} 12px/1 var(--font-mono)`,
              letterSpacing: 'var(--tracking-caps)', textTransform: 'uppercase',
              color: on ? 'var(--ink-0)' : 'var(--ink-2)',
              borderBottom: `2px solid ${on ? 'var(--ink-0)' : 'transparent'}`,
              transition: 'color var(--dur-1) var(--ease-standard)',
            }}
          >
            {t.label}
            {t.count != null && (
              <span style={{
                font: '600 10px/1 var(--font-mono)',
                background: on ? 'var(--paper-0)' : 'var(--paper-0)',
                color: on ? 'var(--ink-0)' : 'var(--ink-2)',
                border: `1px solid ${on ? 'var(--line-strong)' : 'var(--line)'}`, borderRadius: 'var(--r-1)',
                padding: '2px 5px',
              }}>{t.count}</span>
            )}
          </button>
        );
      })}
    </div>
  );
}
