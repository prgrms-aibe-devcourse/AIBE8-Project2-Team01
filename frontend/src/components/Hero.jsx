import { Search } from 'lucide-react';
import ServicePicker from './ServicePicker.jsx';
import RegionPicker from './RegionPicker.jsx';
import RequestPreview from './RequestPreview.jsx';
import FlowDemo from './FlowDemo.jsx';

export default function Hero({ draft, setDraft, slipRef, onStart }) {
  return (
    <section className="hero" aria-labelledby="hero-title">
      <div className="container hero-grid">
        <div className="hero-main">
          <h1 id="hero-title" className="hero-title" translate="no">
            Experts for you!
          </h1>
          <p className="hero-lead">요청서 한 장으로 여러 견적서를 받아보세요</p>

          <form
            className="searchbar"
            ref={slipRef}
            aria-label="요청서 시작"
            onSubmit={(e) => {
              e.preventDefault();
              onStart();
            }}
          >
            <div className="searchbar-field searchbar-service">
              <ServicePicker
                inputId="hero-service"
                label="서비스"
                value={draft.sub}
                onChange={(sub) => setDraft((d) => ({ ...d, sub }))}
              />
            </div>
            <span className="searchbar-divider" aria-hidden="true" />
            <div className="searchbar-field searchbar-region">
              <RegionPicker
                buttonId="hero-region"
                label="지역"
                value={draft.region}
                onChange={(region) => setDraft((d) => ({ ...d, region }))}
              />
            </div>
            <button type="submit" className="btn btn-primary searchbar-submit">
              <Search size={18} strokeWidth={2.4} aria-hidden="true" />
              <span className="submit-label">요청서 작성</span>
            </button>
          </form>
        </div>

        <FlowDemo draft={draft} />
        <RequestPreview draft={draft} />
      </div>
    </section>
  );
}
