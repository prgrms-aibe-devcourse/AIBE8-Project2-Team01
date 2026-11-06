import { categoryIconSrc } from '../data/categoryMeta.js';

// 서비스 분야를 나타내는 입체 아이콘. 메인 화면과 같은 이미지를 기능 화면에서도 쓴다.
export default function CategoryIcon({ secondId, secondName, firstName, size = 48, className = '' }: {
  secondId?: number; secondName?: string; firstName?: string; size?: number; className?: string;
}) {
  return (
    <span className={`cat-ic ${className}`} style={{ width: size, height: size }} aria-hidden="true">
      <img src={categoryIconSrc({ secondId, secondName, firstName })} alt="" width={size} height={size} loading="lazy" />
    </span>
  );
}
