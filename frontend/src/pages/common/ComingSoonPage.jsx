import { Result } from 'antd'

// Chỗ giữ cho các màn hình ở giai đoạn sau; thay bằng trang thật khi làm tới
export function ComingSoonPage({ title }) {
  return <Result status="info" title={title} subTitle="Chức năng này đang được phát triển" />
}
