import { LitElement, html, css } from 'lit';
import { JsonRpc } from 'jsonrpc';

/** "Providers" page — lists every registered Pi4J provider. */
export class QwcPi4jProviders extends LitElement {
    static styles = css`
        :host { display: block; padding: 15px; }
        .error { color: var(--lumo-error-text-color); white-space: pre-wrap; }
        h3 { margin: 1em 0 0.25em; }
        table { border-collapse: collapse; width: 100%; font-size: var(--lumo-font-size-s); }
        th, td { text-align: left; padding: 4px 8px; border-bottom: 1px solid var(--lumo-contrast-10pct); }
        th { color: var(--lumo-secondary-text-color); font-weight: 600; }
        .empty { color: var(--lumo-secondary-text-color); font-style: italic; }
    `;

    static properties = {
        _providers: { state: true },
        _error: { state: true },
    };

    constructor() {
        super();
        this._providers = null;
        this._error = null;
        this.jsonRpc = new JsonRpc(this);
    }

    connectedCallback() {
        super.connectedCallback();
        this.jsonRpc.getProviders()
            .then(r => { this._providers = (r && r.result !== undefined) ? r.result : r; })
            .catch(error => {
                this._error = error;
                console.error('qwc-pi4j-providers getProviders failed', error);
            });
    }

    render() {
        if (this._error) {
            return html`<div class="error">getProviders() failed:
${JSON.stringify(this._error, Object.getOwnPropertyNames(this._error), 2)}</div>`;
        }
        if (this._providers === null) {
            return html`<div>Loading…</div>`;
        }
        return html`
            <div class="container">
                <h3>Providers</h3>
                ${this._providers.length === 0
                    ? html`<div class="empty">None registered</div>`
                    : html`
                        <table>
                            <tr><th>ID</th><th>Name</th></tr>
                            ${this._providers.map(p => html`<tr><td>${p.id}</td><td>${p.name}</td></tr>`)}
                        </table>`}
            </div>`;
    }
}

customElements.define('qwc-pi4j-providers', QwcPi4jProviders);
