import { Alert, Table, Typography } from 'antd'
import { useQuery } from '@tanstack/react-query'
import { publicApi } from '../../api/publicApi'
import { errorMessage } from '../../utils/formErrors'
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
  const { data, isLoading, error } = useQuery({ queryKey: ['public', 'services'], queryFn: publicApi.services })

  return (
    <>
      <Typography.Title level={2}>Dịch vụ và bảng giá</Typography.Title>
      {error && <Alert type="error" showIcon message={errorMessage(error)} style={{ marginBottom: 16 }} />}
      <Table rowKey="maDichVu" columns={COLUMNS} dataSource={data} loading={isLoading} pagination={false} />
    </>
  )
}
