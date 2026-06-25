import { LitElement, html, css } from 'lit';
import { JsonRpc } from 'jsonrpc';

/** "Platform" page — shows the active Pi4J platform name. */
export class QwcPi4jPlatform extends LitElement {
    static styles = css`
        :host { display: block; padding: 15px; }
        .error { color: var(--lumo-error-text-color); white-space: pre-wrap; }
        h3 { margin: 1em 0 0.25em; }
        .platform { font-weight: 600; color: var(--lumo-primary-text-color); }
    `;

    static properties = {
        _platform: { state: true },
        _error: { state: true },
    };

    constructor() {
        super();
        this._platform = null;
        this._error = null;
        this.jsonRpc = new JsonRpc(this);
    }

    connectedCallback() {
        super.connectedCallback();
        this.jsonRpc.getActivePlatform()
            .then(r => { this._platform = (r && r.result !== undefined) ? r.result : r; })
            .catch(error => {
                this._error = error;
                console.error('qwc-pi4j-platform getActivePlatform failed', error);
            });
    }

    render() {
        if (this._error) {
            return html`<div class="error">getActivePlatform() failed:
${JSON.stringify(this._error, Object.getOwnPropertyNames(this._error), 2)}</div>`;
        }
        if (this._platform === null) {
            return html`<div>Loading…</div>`;
        }
        return html`
            <div class="container">
                <h3>Active platform</h3>
                <div class="platform">${this._platform}</div>
            </div>`;
    }
}

customElements.define('qwc-pi4j-platform', QwcPi4jPlatform);
