import React from 'react';

/**
 * RallyOn Checkbox — square check with hard seam.
 */
export function Checkbox({ label, checked, defaultChecked, disabled = false, onChange, style, ...rest }) {
  const isControlled = checked !== undefined;
  const [internal, setInternal] = React.useState(!!defaultChecked);
  const on = isControlled ? checked : internal;

  const toggle = (e) => {
    if (disabled) return;
    if (!isControlled) setInternal(e.target.checked);
    onChange && onChange(e);
  };

  return (
    <label style={{
      display: 'inline-flex', alignItems: 'center', gap: '10px',
      cursor: disabled ? 'not-allowed' : 'pointer', opacity: disabled ? 0.45 : 1,
      font: 'var(--fw-regular) 14px/1 var(--font-sans)', color: 'var(--ink-0)', ...style,
    }}>
      <span style={{ position: 'relative', display: 'inline-flex', width: 18, height: 18 }}>
        <input
          type="checkbox"
          checked={on}
          disabled={disabled}
          onChange={toggle}
          style={{ position: 'absolute', opacity: 0, width: 18, height: 18, margin: 0, cursor: 'inherit' }}
          {...rest}
        />
        <span style={{
          width: 18, height: 18,
          background: on ? 'var(--ink-0)' : 'var(--paper-2)',
          border: `var(--bw) solid ${on ? 'var(--line-strong)' : 'var(--line)'}`,
          borderRadius: 'var(--r-1)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          transition: 'var(--transition-ui)',
        }}>
          {on && (
            <svg width="12" height="12" viewBox="0 0 12 12" fill="none" stroke="var(--paper-1)" strokeWidth="2.4" strokeLinecap="square">
              <path d="M2.5 6.2 L5 8.6 L9.5 3.5" />
            </svg>
          )}
        </span>
      </span>
      {label && <span>{label}</span>}
    </label>
  );
}
