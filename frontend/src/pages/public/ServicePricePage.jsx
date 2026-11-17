import { useEffect } from 'react'
import { Alert, Table, Typography } from 'antd'
import { useDispatch, useSelector } from 'react-redux'
import { fetchServices } from '../../store/servicesSlice'
import { formatVnd } from '../../utils/format'

const COLUMNS = [
  {
    title: 'Dịch vụ',
    dataIndex: 'tenDichVu',
    render: (name, row) => (
      <>
        <Typography.Text strong>{name}</Typography.Text>
        {row.moTa && <Typography.Paragraph type="secondary" style={{ marginBottom: 0 }}>{row.moTa}</Typography.Paragraph>}
      </>
    ),
  },
  { title: 'Thời lượng', dataIndex: 'thoiLuong', width: 120, responsive: ['sm'], render: (minutes) => (minutes ? `${minutes} phút` : '—') },
  { title: 'Giá', dataIndex: 'gia', width: 150, align: 'right', render: formatVnd },
]

export function ServicePricePage() {
  const dispatch = useDispatch()
  const { items, status, error } = useSelector((state) => state.services)

  useEffect(() => {
    dispatch(fetchServices())
  }, [dispatch])

  return (
    <>
      <Typography.Title level={2}>Dịch vụ và bảng giá</Typography.Title>
      {error && <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} />}
      <Table
        rowKey="maDichVu"
        columns={COLUMNS}
        dataSource={items}
        loading={status === 'idle' || status === 'loading'}
        pagination={false}
      />
    </>
  )
}
