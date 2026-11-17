import { Alert, Avatar, Card, Col, Empty, Row, Skeleton, Typography } from 'antd'
import { UserOutlined } from '@ant-design/icons'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { publicApi } from '../../api/publicApi'
import { errorMessage } from '../../utils/formErrors'

export function DoctorListPage() {
  const { data, isLoading, error } = useQuery({ queryKey: ['public', 'doctors'], queryFn: publicApi.doctors })

  return (
    <>
      <Typography.Title level={2}>Đội ngũ bác sĩ</Typography.Title>
      {error && <Alert type="error" showIcon message={errorMessage(error)} />}
      {isLoading && <Skeleton active avatar paragraph={{ rows: 3 }} />}
      {data && data.length === 0 && <Empty description="Chưa có bác sĩ" />}
      <Row gutter={[16, 16]}>
        {data?.map((doctor) => (
          <Col key={doctor.maBacSi} xs={24} sm={12} lg={8}>
            <Link to={`/bac-si/${doctor.maBacSi}`}>
              <Card hoverable>
                <Card.Meta
                  avatar={<Avatar size={64} src={doctor.anhDaiDien} icon={<UserOutlined />} />}
                  title={doctor.hoTen}
                  description={doctor.chuyenMon ?? 'Nha khoa tổng quát'}
                />
              </Card>
            </Link>
          </Col>
        ))}
      </Row>
    </>
  )
}
