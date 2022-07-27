package pro1041.ui;

import java.awt.Font;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import pro1041.dao.BillDAO;
import pro1041.dao.DeliveryDAO;
import pro1041.dao.OrderDetailDAO;
import pro1041.dao.ProductDetailDAO;
import pro1041.entity.Bill;
import pro1041.entity.Delivery;
import pro1041.entity.OrderDetail;
import pro1041.entity.PaymentMethod;
import pro1041.entity.Status;
import pro1041.utils.Auth;
import pro1041.utils.DateHelper;
import pro1041.utils.JdbcHelper;
import pro1041.utils.MoneyFormat;
import pro1041.utils.MsgBox;
import pro1041.utils.Validator;

public class DeliveryManagementFrame extends javax.swing.JInternalFrame {

    private DeliveryDAO deliveryDAO;
    private BillDAO billDAO;
    private OrderDetailDAO orderDetailDAO;
    private ProductDetailDAO productDetailDAO;
    private List<Delivery> listDelivery;
    private DefaultTableModel deliveryTableModel;
    private DefaultTableModel orderDetailTableModel;

    private int index;

    private final String[] cboOrderByModel = new String[]{"ID", "CUSTOMER_NAME", "EMAIL", "PHONE_NUMBER", "ADDRESS", "STATUS", "UPDATE_TIME"};
    private final String[] cboOrderDirectionModel = new String[]{"DESC", "ASC"};

    /**
     * Creates new form DeliveryManagementFrame
     */
    public DeliveryManagementFrame() {
        initComponents();
        init();
    }

    private void init() {
        deliveryDAO = new DeliveryDAO();
        billDAO = new BillDAO();
        orderDetailDAO = new OrderDetailDAO();
        productDetailDAO = new ProductDetailDAO();
        deliveryTableModel = (DefaultTableModel) tblDelivery.getModel();
        orderDetailTableModel = (DefaultTableModel) tblOrderDetail.getModel();
        tblDelivery.getTableHeader().setFont(new Font("Tahoma", 1, 16));
        tblOrderDetail.getTableHeader().setFont(new Font("Tahoma", 1, 16));
        listDelivery = deliveryDAO.selectAll();
        fillToDeliveryTable(listDelivery);

        for (Status status : Status.values()) {
            cboStatus.addItem(status.getValue());
        }

        tblDelivery.getSelectionModel().addListSelectionListener((e) -> {
            updateTableIndex();
        });

        clear();
    }

    private void clear() {
        txtID.setText("");
        txtCustomerName.setText("");
        txtEmail.setText("");
        txtPhoneNumber.setText("");
        txtAddress.setText("");
        txtEmail.setText("");
        lblStatus.setText("");
        lblTotal.setText("0 đ");
        palController.setVisible(false);
    }

    private void showDetail(Delivery delivery) {
        txtID.setText(String.valueOf(delivery.getId()));
        txtCustomerName.setText(delivery.getCustomerName());
        txtEmail.setText(delivery.getEmail());
        txtPhoneNumber.setText(delivery.getPhoneNumber());
        txtAddress.setText(delivery.getAddress());
        txtEmail.setText(delivery.getEmail());
        lblStatus.setText(delivery.getStatus().getValue());
        lblTotal.setText("0 đ");
        palController.setVisible(true);
        updateStatus(delivery.getStatus());
        fillToOrderDetailTable(delivery.getOrderID());
        jTabbedPane1.setSelectedIndex(1);
    }

    private void updateStatus(Status status) {
        btnUpdate.setVisible(status == Status.ORDERED);
        btnToShip.setVisible(status == Status.ORDERED);
        btnCancelled.setVisible(status == Status.ORDERED || status == Status.TOSHIP);
        btnReceived.setVisible(status == Status.TOSHIP);

        boolean check = status == Status.ORDERED;

        txtCustomerName.setEditable(check);
        txtEmail.setEditable(check);
        txtPhoneNumber.setEditable(check);
        txtAddress.setEditable(check);
        txtNote.setEditable(check);
    }

    private void updateTableIndex() {
        lblIndex.setText((tblDelivery.getSelectedRow() + 1) + " / " + listDelivery.size());
    }

    private Delivery getDeliveryByForm() {
        if (Validator.isNull(txtCustomerName, "Vui lòng nhập tên khách hàng !")
                || Validator.isNull(txtPhoneNumber, "Vui lòng nhập số điện thoại khách hàng !")
                || Validator.isNull(txtAddress, "Vui lòng nhập địa chỉ khách hàng !")
                || Validator.isValidPhone(txtPhoneNumber, "Số điện thoại không đúng định dạng !")) {
            return null;
        }
        return new Delivery(listDelivery.get(index).getId(), txtCustomerName.getText(), txtEmail.getText(), txtPhoneNumber.getText(), txtAddress.getText(), txtNote.getText(), Status.ORDERED, new Timestamp(DateHelper.now().getTime()), listDelivery.get(index).getOrderID());
    }

    private void fillToDeliveryTable(Delivery delivery) {
        deliveryTableModel.addRow(new Object[]{delivery.getId(), delivery.getCustomerName(), delivery.getEmail(),
            delivery.getPhoneNumber(), delivery.getAddress(), delivery.getStatus().getValue(),
            DateHelper.toString(delivery.getUpdateTime())});
    }

    private void fillToDeliveryTable(List<Delivery> list) {
        deliveryTableModel.setRowCount(0);
        for (Delivery delivery : list) {
            fillToDeliveryTable(delivery);
        }
        updateTableIndex();
    }

    private void fillToDeliveryTable(int index, Delivery delivery) {
        deliveryTableModel.setValueAt(delivery.getId(), index, 0);
        deliveryTableModel.setValueAt(delivery.getCustomerName(), index, 1);
        deliveryTableModel.setValueAt(delivery.getEmail(), index, 2);
        deliveryTableModel.setValueAt(delivery.getPhoneNumber(), index, 3);
        deliveryTableModel.setValueAt(delivery.getAddress(), index, 4);
        deliveryTableModel.setValueAt(delivery.getStatus().getValue(), index, 5);
        deliveryTableModel.setValueAt(DateHelper.toString(delivery.getUpdateTime()), index, 6);
    }

    private void fillToOrderDetailTable(int orderID) {
        String orderDetailSQL = "SELECT P.ID, P.NAME, P.BRAND_ID, PD.TYPE, PD.SIZE, OD.PRICE, OD.QUANTITY, OD.QUANTITY * OD.PRICE FROM ORDER_DETAIL OD INNER JOIN PRODUCT_DETAIL PD ON OD.PRODUCT_DETAIL_ID = PD.ID INNER JOIN PRODUCT P ON PD.PRODUCT_ID = P.ID WHERE ORDER_ID = ?";
        orderDetailTableModel.setRowCount(0);
        List<Object[]> listOrderDetail = JdbcHelper.getListOfArray(orderDetailSQL, 8, orderID);

        double total = 0;

        for (Object[] obj : listOrderDetail) {
            orderDetailTableModel.addRow(new Object[]{obj[0], obj[1], obj[2], obj[3], obj[4], MoneyFormat.formatVND(obj[5]), obj[6], MoneyFormat.formatVND(obj[7])});
            total += ((BigDecimal) obj[7]).doubleValue();
        }
        lblTotal.setText(MoneyFormat.formatVND(total));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        lblIndex = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblDelivery = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        cboStatus = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        btnShowAll = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        cboOrderBy = new javax.swing.JComboBox<>();
        cboOrderDirection = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblOrderDetail = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txtCustomerName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtPhoneNumber = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAddress = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtNote = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        palController = new javax.swing.JPanel();
        btnUpdate = new javax.swing.JButton();
        btnToShip = new javax.swing.JButton();
        btnCancelled = new javax.swing.JButton();
        btnReceived = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

        setClosable(true);
        setIconifiable(true);
        setTitle("ShoeStoreSys - Quản Lý Giao Hàng");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logoExtraSmall.jpg"))); // NOI18N
        setPreferredSize(new java.awt.Dimension(1200, 800));

        jTabbedPane1.setFocusable(false);
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        lblIndex.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblIndex.setForeground(new java.awt.Color(255, 102, 102));
        lblIndex.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIndex.setText("Index");
        lblIndex.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        tblDelivery.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblDelivery.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Đơn", "Tên Khách Hàng", "Email", "Số Điện Thoại", "Địa Chỉ", "Trạng Thái", "Thời Gian Cập Nhật"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDelivery.setRowHeight(30);
        tblDelivery.getTableHeader().setReorderingAllowed(false);
        tblDelivery.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblDeliveryMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(tblDelivery);

        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        cboStatus.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất Cả" }));
        cboStatus.setFocusable(false);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel10.setText("Trạng Thái Đơn Hàng");

        btnSearch.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/search-30.png"))); // NOI18N
        btnSearch.setText("Tìm Kiếm");
        btnSearch.setFocusable(false);
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnShowAll.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnShowAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/refresh-30.png"))); // NOI18N
        btnShowAll.setText("Hiển Thị Tất Cả");
        btnShowAll.setFocusable(false);
        btnShowAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowAllActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel12.setText("Sắp Xếp Theo");

        cboOrderBy.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboOrderBy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mã Đơn", "Tên Khách Hàng", "Email", "Số Điện Thoại", "Địa Chỉ", "Trạng Thái", "Thời Gian Cập Nhật" }));
        cboOrderBy.setFocusable(false);

        cboOrderDirection.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboOrderDirection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Giảm Dần", "Tăng Dần" }));
        cboOrderDirection.setFocusable(false);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel9.setText("Từ Khóa");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addGap(18, 18, 18)
                                        .addComponent(cboOrderBy, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cboOrderDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnShowAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(btnSearch)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnShowAll, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(cboOrderBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboOrderDirection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnSearch, cboOrderBy, cboOrderDirection, cboStatus, txtSearch});

        jTabbedPane1.addTab("Danh Sách", jPanel1);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel1.setText("Mã Đơn Hàng");

        txtID.setEditable(false);
        txtID.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 153, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("THÔNG TIN ĐƠN GIAO HÀNG");

        tblOrderDetail.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblOrderDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã", "Tên", "Hãng", "Loại", "Cỡ", "Giá", "Số Lượng", "Tiền Hàng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblOrderDetail.setRowHeight(30);
        tblOrderDetail.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblOrderDetail.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tblOrderDetail);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel2.setText("Tên Khách Hàng ");

        txtCustomerName.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel3.setText("Email");

        txtEmail.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel4.setText("Số Điện Thoại");

        txtPhoneNumber.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel5.setText("Địa Chỉ");

        txtAddress.setColumns(20);
        txtAddress.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtAddress.setRows(5);
        txtAddress.setMargin(new java.awt.Insets(10, 10, 2, 2));
        jScrollPane1.setViewportView(txtAddress);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel6.setText("Ghi Chú");

        txtNote.setColumns(20);
        txtNote.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtNote.setRows(5);
        txtNote.setMargin(new java.awt.Insets(10, 10, 2, 2));
        jScrollPane2.setViewportView(txtNote);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel7.setText("TRẠNG THÁI : ");

        lblStatus.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(255, 0, 0));
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        lblTotal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(255, 0, 0));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal.setText("0 VNĐ");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel11.setText("Tổng Giá Trị : ");

        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 10);
        flowLayout1.setAlignOnBaseline(true);
        palController.setLayout(flowLayout1);

        btnUpdate.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/update-40.png"))); // NOI18N
        btnUpdate.setText("CẬP NHẬT THÔNG TIN");
        btnUpdate.setFocusable(false);
        btnUpdate.setPreferredSize(new java.awt.Dimension(250, 50));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        palController.add(btnUpdate);

        btnToShip.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnToShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/delivery-scooter-30.png"))); // NOI18N
        btnToShip.setText("BẮT ĐẦU GIAO HÀNG");
        btnToShip.setFocusable(false);
        btnToShip.setPreferredSize(new java.awt.Dimension(240, 50));
        btnToShip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToShipActionPerformed(evt);
            }
        });
        palController.add(btnToShip);

        btnCancelled.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnCancelled.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/cancel-40.png"))); // NOI18N
        btnCancelled.setText("XÁC NHẬN HỦY ĐƠN");
        btnCancelled.setFocusable(false);
        btnCancelled.setPreferredSize(new java.awt.Dimension(240, 50));
        btnCancelled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelledActionPerformed(evt);
            }
        });
        palController.add(btnCancelled);

        btnReceived.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnReceived.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/payment-30.png"))); // NOI18N
        btnReceived.setText("XÁC NHẬN NHẬN HÀNG");
        btnReceived.setFocusable(false);
        btnReceived.setPreferredSize(new java.awt.Dimension(255, 50));
        btnReceived.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceivedActionPerformed(evt);
            }
        });
        palController.add(btnReceived);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 13, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel7)))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel6)
                                                    .addComponent(jLabel5)))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(txtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                        .addGap(24, 24, 24)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                                    .addComponent(jScrollPane1)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(palController, javax.swing.GroupLayout.PREFERRED_SIZE, 1141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(jLabel5)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addComponent(jScrollPane1))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jLabel6)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(palController, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jTabbedPane1.addTab("Chi Tiết Đơn Giao", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1184, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblDeliveryMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDeliveryMouseReleased
        if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2) {
            index = tblDelivery.getSelectedRow();
            showDetail(listDelivery.get(index));
        }
    }//GEN-LAST:event_tblDeliveryMouseReleased

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        Delivery delivery = getDeliveryByForm();
        if (delivery == null) {
            return;
        }
        deliveryDAO.update(delivery);
        listDelivery.set(index, delivery);
        fillToDeliveryTable(index, delivery);
        MsgBox.alert(null, "Cập nhật thông tin đơn hàng thành công !");
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnToShipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToShipActionPerformed
        if (MsgBox.confirm(null, "Xác nhận đơn bắt đầu giao ?")) {
            Delivery delivery = listDelivery.get(index);
            delivery.setStatus(Status.TOSHIP);
            delivery.setUpdateTime(new Timestamp(new Date().getTime()));
            deliveryDAO.update(delivery);
            fillToDeliveryTable(index, delivery);
            showDetail(delivery);
        }
    }//GEN-LAST:event_btnToShipActionPerformed

    private void btnCancelledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelledActionPerformed
        if (MsgBox.confirm(null, "Xác nhận đơn đã bị hủy ?")) {
            Delivery delivery = listDelivery.get(index);
            delivery.setStatus(Status.CANCELLED);
            delivery.setUpdateTime(new Timestamp(new Date().getTime()));
            deliveryDAO.update(delivery);
            List<OrderDetail> list = orderDetailDAO.selectAllByOrderID(delivery.getOrderID());
            for (OrderDetail orderDetail : list) {
                productDetailDAO.updateQuantityByID(orderDetail.getQuantity(), orderDetail.getProductDetailID());
            }
            fillToDeliveryTable(index, delivery);
            showDetail(delivery);
        }
    }//GEN-LAST:event_btnCancelledActionPerformed

    private void btnReceivedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceivedActionPerformed
        if (MsgBox.confirm(null, "Xác nhận khách đã nhận được hàng ?")) {
            Delivery delivery = listDelivery.get(index);
            delivery.setStatus(Status.RECEIVED);
            delivery.setUpdateTime(new Timestamp(new Date().getTime()));
            deliveryDAO.update(delivery);
            fillToDeliveryTable(index, delivery);
            if (JdbcHelper.getListOfArray("SELECT ID FROM BILL WHERE ORDER_ID = ?", 1, delivery.getOrderID()).size() == 0) {
                Bill bill = new Bill(delivery.getUpdateTime(), PaymentMethod.CASH, Auth.getUser().getId(), delivery.getOrderID());
                billDAO.insert(bill);
            }
            showDetail(delivery);
        }
    }//GEN-LAST:event_btnReceivedActionPerformed

    private void btnShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowAllActionPerformed
        listDelivery = deliveryDAO.selectAll();
        fillToDeliveryTable(listDelivery);
        cboStatus.setSelectedIndex(0);
        cboOrderBy.setSelectedIndex(0);
        cboOrderDirection.setSelectedIndex(0);
        clear();
    }//GEN-LAST:event_btnShowAllActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String search = txtSearch.getText();
        String sql = "SELECT * FROM DELIVERY";
        String orderBy = " ORDER BY " + cboOrderByModel[cboOrderBy.getSelectedIndex()] + " " + cboOrderDirectionModel[cboOrderDirection.getSelectedIndex()];
        if (search.isEmpty()) {
            if (cboStatus.getSelectedIndex() > 0) {
                sql += " WHERE STATUS = '" + Status.values()[cboStatus.getSelectedIndex() - 1] + "'";
            }
            listDelivery = deliveryDAO.selectBySQL(sql + orderBy);
        } else {
            search = "%" + search + "%";
            sql += " WHERE ( ID LIKE ? OR CUSTOMER_NAME LIKE ? OR PHONE_NUMBER LIKE ? OR EMAIL LIKE ? OR ADDRESS LIKE ? )";
            if (cboStatus.getSelectedIndex() > 0) {
                sql += " AND STATUS = '" + Status.values()[cboStatus.getSelectedIndex() - 1] + "'";
            }
            listDelivery = deliveryDAO.selectBySQL(sql + orderBy, search, search, search, search, search);
        }
        fillToDeliveryTable(listDelivery);
    }//GEN-LAST:event_btnSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelled;
    private javax.swing.JButton btnReceived;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnShowAll;
    private javax.swing.JButton btnToShip;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cboOrderBy;
    private javax.swing.JComboBox<String> cboOrderDirection;
    private javax.swing.JComboBox<String> cboStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblIndex;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel palController;
    private javax.swing.JTable tblDelivery;
    private javax.swing.JTable tblOrderDetail;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtCustomerName;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextArea txtNote;
    private javax.swing.JTextField txtPhoneNumber;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
