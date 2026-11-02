// 단계 표시와 화면 제목
export function StepHeader({ step, label, title }: { step: number; label: string; title: string }) {
  return (
    <div>
      <div className="step">
        {step} / 4 · {label}
      </div>
      <h1>{title}</h1>
    </div>
  );
}
