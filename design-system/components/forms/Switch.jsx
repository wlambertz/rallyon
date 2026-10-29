import React from 'react';

/**
 * RallyOn Switch — mechanical on/off toggle. No spring; 120ms slide.
 */
export function Switch({ label, checked, defaultChecked, disabled = false, onChange, style, ...rest }) {
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
      <span style={{ position: 'relative', display: 'inline-flex', width: 40, height: 22 }}>
        <input
          type="checkbox"
          checked={on}
          disabled={disabled}
          onChange={toggle}
          style={{ position: 'absolute', opacity: 0, width: 40, height: 22, margin: 0, cursor: 'inherit' }}
          {...rest}
        />
        <span style={{
          width: 40, height: 22,
          background: on ? 'var(--ink-0)' : 'var(--paper-0)',
          border: `var(--bw) solid var(--line-strong)`,
          borderRadius: 'var(--r-pill)',
          transition: 'background var(--dur-1) var(--ease-standard)',
          display: 'flex', alignItems: 'center',
        }}>
          <span style={{
            position: 'absolute', top: '50%', transform: 'translateY(-50%)', left: on ? 20 : 2,
            width: 16, height: 16,
            background: on ? 'var(--paper-1)' : 'var(--ink-0)',
            border: 'var(--bw) solid var(--line-strong)',
            borderRadius: 'var(--r-pill)',
            transition: 'left var(--dur-1) var(--ease-standard)',
          }} />
        </span>
      </span>
      {label && <span>{label}</span>}
    </label>
  );
}
