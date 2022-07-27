package pro1041.ui;

import java.sql.Timestamp;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import pro1041.dao.BrandDAO;
import pro1041.dao.ExchangeProductDAO;
import pro1041.dao.OrderDetailDAO;
import pro1041.dao.ProductDetailDAO;
import pro1041.entity.ExchangeProduct;
import pro1041.entity.OrderDetail;
import pro1041.entity.ProductDetail;
import pro1041.utils.Auth;
import pro1041.utils.DateHelper;
import pro1041.utils.ImageHelper;
import pro1041.utils.MoneyFormat;
import pro1041.utils.MsgBox;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class ExchangeProductDialog extends javax.swing.JDialog {

    private BrandDAO brandDAO;
    private ProductDetailDAO productDetailDAO;
    private ExchangeProductDAO exchangeProductDAO;
    private OrderDetailDAO orderDetailDAO;

    private List<ProductDetail> listProductDetail;
    private DefaultTableModel tableModel;

    private Object[] obj;
    private OrderDetail orderDetail;
    private int billID;

    private boolean succeeded;

    public ExchangeProductDialog(java.awt.Frame parent, boolean modal, Object[] obj, OrderDetail orderDetail, int billID) {
        super(parent, modal);
        this.obj = obj;
        this.billID = billID;
        this.orderDetail = orderDetail;
        setIconImage(ImageHelper.getAppIcon());
        initComponents();
        init();
    }

    private void init() {
        brandDAO = new BrandDAO();
        productDetailDAO = new ProductDetailDAO();
        exchangeProductDAO = new ExchangeProductDAO();
        orderDetailDAO = new OrderDetailDAO();

        tableModel = (DefaultTableModel) tblProductDetail.getModel();

        lblID.setText(String.valueOf(obj[0]));
        lblName.setText(String.valueOf(obj[1]));
        lblBrand.setText(brandDAO.selectByID((int) obj[2]).getName());
        lblType.setText(String.valueOf(obj[3]));
        lblSize.setText(String.valueOf(obj[4]));
        lblPrice.setText(MoneyFormat.formatVND(obj[5]));

        String sql = "SELECT * FROM PRODUCT_DETAIL WHERE PRODUCT_ID = ? AND QUANTITY > 0";
        listProductDetail = productDetailDAO.selectBySQL(sql, obj[0]);
        fillToTable(listProductDetail);
        succeeded = false;
    }

    private void fillToTable(ProductDetail productDetail) {
        tableModel.addRow(new Object[]{productDetail.getType(), productDetail.getSize(), MoneyFormat.formatVND(productDetail.getPrice()), productDetail.getQuantity()});
    }

    private void fillToTable(List<ProductDetail> list) {
        tableModel.setRowCount(0);
        for (ProductDetail productDetail : list) {
            fillToTable(productDetail);
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblID = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblType = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblSize = new javax.swing.JLabel();
        lblBrand = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblPrice = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProductDetail = new javax.swing.JTable();
        btnDoiHang = new javax.swing.JButton();
        btnReturn = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtNote = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ShoeStoreSys - Đổi Sản Phẩm");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 153, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("ĐỔI SẢN PHẨM");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel2.setText("Tên Sản Phẩm");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel3.setText("Mã Sản Phẩm");

        lblID.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        lblName.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel6.setText("Loại");

        lblType.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel8.setText("Kích Cỡ");

        lblSize.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        lblBrand.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel11.setText("Hãng");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel12.setText("Giá");

        lblPrice.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        tblProductDetail.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblProductDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Loại", "Cỡ", "Giá", "Số Lượng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProductDetail.setRowHeight(30);
        tblProductDetail.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblProductDetail);

        btnDoiHang.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnDoiHang.setText("Đổi Hàng");
        btnDoiHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDoiHangActionPerformed(evt);
            }
        });

        btnReturn.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnReturn.setText("Trở Về");
        btnReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 153, 255));
        jLabel14.setText("SẢN PHẨM CẦN ĐỔI");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 153, 255));
        jLabel15.setText("SẢN PHẨM HIỆN CÓ");

        txtNote.setColumns(20);
        txtNote.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtNote.setRows(5);
        jScrollPane2.setViewportView(txtNote);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Ghi Chú");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1176, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel11))
                        .addGap(77, 77, 77)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblName, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(156, 156, 156)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblSize, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lblID, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblType, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnDoiHang, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnReturn, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1020, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblID, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblType, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(lblSize, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(lblBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDoiHang, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReturn, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, jLabel12, jLabel2, jLabel3, jLabel6, jLabel8, lblBrand, lblID, lblName, lblPrice, lblSize, lblType});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnDoiHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoiHangActionPerformed
        int index = tblProductDetail.getSelectedRow();
        if (index == -1) {
            MsgBox.alert(null, "Chưa chọn loại sản phẩm cần đổi !");
            return;
        }

        int oProductDetailID = orderDetail.getProductDetailID();
        int nProductDetailID = listProductDetail.get(index).getId();

        if (oProductDetailID != nProductDetailID) {
            List<OrderDetail> list = orderDetailDAO.selectBySQL("SELECT * FROM ORDER_DETAIL WHERE ORDER_ID = ? AND PRODUCT_DETAIL_ID = ?", orderDetail.getOrderID(), nProductDetailID);
            OrderDetail od = list.size() > 0 ? list.get(0) : null;

            if (od != null) {
                od.setQuantity(od.getQuantity() + 1);
                orderDetailDAO.update(od);
            } else {
                OrderDetail newOrderDetail = new OrderDetail(1, listProductDetail.get(index).getPrice(), nProductDetailID, orderDetail.getOrderID());
                orderDetailDAO.insert(newOrderDetail);
            }

            if (orderDetail.getQuantity() == 1) {
                orderDetailDAO.delete(orderDetail.getId());
            } else {
                orderDetail.setQuantity(orderDetail.getQuantity() - 1);
                orderDetailDAO.update(orderDetail);
            }

            productDetailDAO.updateQuantityByID(1, oProductDetailID);
            productDetailDAO.updateQuantityByID(-1, nProductDetailID);
        }

        ExchangeProduct ep = new ExchangeProduct(new Timestamp(DateHelper.now().getTime()), txtNote.getText(), oProductDetailID, nProductDetailID, billID, Auth.getUser().getId());
        exchangeProductDAO.insert(ep);
        MsgBox.alert(null, "Đổi trả hàng thành công !");
        succeeded = true;
        dispose();
    }//GEN-LAST:event_btnDoiHangActionPerformed

    private void btnReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnActionPerformed
        dispose();
    }//GEN-LAST:event_btnReturnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDoiHang;
    private javax.swing.JButton btnReturn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblBrand;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPrice;
    private javax.swing.JLabel lblSize;
    private javax.swing.JLabel lblType;
    private javax.swing.JTable tblProductDetail;
    private javax.swing.JTextArea txtNote;
    // End of variables declaration//GEN-END:variables

}
