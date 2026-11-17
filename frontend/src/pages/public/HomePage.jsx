import { Button, Card, Col, Row, Space, Typography } from 'antd'
import { CalendarOutlined, MedicineBoxOutlined, SafetyCertificateOutlined } from '@ant-design/icons'
import { Link } from 'react-router-dom'

const HIGHLIGHTS = [
  { icon: <CalendarOutlined />, title: 'Đặt lịch online', text: 'Chọn bác sĩ và khung giờ 30 phút còn trống, không cần gọi điện.' },
  { icon: <MedicineBoxOutlined />, title: 'Đội ngũ bác sĩ', text: 'Bác sĩ nhiều chuyên môn, hồ sơ và lộ trình điều trị được theo dõi xuyên suốt.' },
  { icon: <SafetyCertificateOutlined />, title: 'Nhắc tái khám', text: 'Hệ thống tự nhắc bạn khi đến lịch tái khám theo lộ trình điều trị.' },
]

export function HomePage() {
  return (
    <Space direction="vertical" size={32} style={{ width: '100%' }}>
      <div style={{ textAlign: 'center', padding: '48px 0' }}>
        <Typography.Title>Chăm sóc răng miệng chuyên nghiệp</Typography.Title>
        <Typography.Paragraph type="secondary" style={{ fontSize: 16 }}>
          Đặt lịch khám nha khoa trực tuyến, theo dõi hồ sơ và lộ trình điều trị của bạn.
        </Typography.Paragraph>
        <Space wrap style={{ justifyContent: 'center' }}>
          <Link to="/dang-ky"><Button type="primary" size="large">Đăng ký để đặt lịch</Button></Link>
          <Link to="/bac-si"><Button size="large">Xem bác sĩ</Button></Link>
          <Link to="/dich-vu"><Button size="large">Bảng giá dịch vụ</Button></Link>
        </Space>
      </div>
      <Row gutter={[16, 16]}>
        {HIGHLIGHTS.map((item) => (
          <Col key={item.title} xs={24} md={8}>
            <Card>
              <Typography.Title level={4}>{item.icon} {item.title}</Typography.Title>
              <Typography.Paragraph type="secondary" style={{ marginBottom: 0 }}>{item.text}</Typography.Paragraph>
            </Card>
          </Col>
        ))}
      </Row>
    </Space>
  )
}
