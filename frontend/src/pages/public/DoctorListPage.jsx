import { useEffect } from 'react'
import { Alert, Avatar, Card, Col, Empty, Row, Skeleton, Typography } from 'antd'
import { UserOutlined } from '@ant-design/icons'
import { useDispatch, useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { fetchDoctors } from '../../store/doctorsSlice'

export function DoctorListPage() {
  const dispatch = useDispatch()
  const { items, status, error } = useSelector((state) => state.doctors.list)

  useEffect(() => {
    dispatch(fetchDoctors())
  }, [dispatch])

  const isLoading = status === 'idle' || status === 'loading'

  return (
    <>
      <Typography.Title level={2}>Đội ngũ bác sĩ</Typography.Title>
      {error && <Alert type="error" showIcon message={error} />}
      {isLoading && <Skeleton active avatar paragraph={{ rows: 3 }} />}
      {status === 'succeeded' && items.length === 0 && <Empty description="Chưa có bác sĩ" />}
      <Row gutter={[16, 16]}>
        {items.map((doctor) => (
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
