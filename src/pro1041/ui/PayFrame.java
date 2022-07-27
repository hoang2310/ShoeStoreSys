package pro1041.ui;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import pro1041.dao.BillDAO;
import pro1041.dao.BrandDAO;
import pro1041.dao.DeliveryDAO;
import pro1041.dao.ExchangeProductDAO;
import pro1041.dao.OrderDAO;
import pro1041.dao.OrderDetailDAO;
import pro1041.dao.ProductDAO;
import pro1041.dao.ProductDetailDAO;
import pro1041.entity.Bill;
import pro1041.entity.Brand;
import pro1041.entity.Delivery;
import pro1041.entity.ExchangeProduct;
import pro1041.entity.Order;
import pro1041.entity.OrderDetail;
import pro1041.entity.PaymentMethod;
import pro1041.entity.Product;
import pro1041.entity.ProductDetail;
import pro1041.utils.Auth;
import pro1041.utils.DateHelper;
import pro1041.utils.JdbcHelper;
import pro1041.utils.MoneyFormat;
import pro1041.utils.MsgBox;
import pro1041.utils.PDFHelper;

/**
 *
 * @author MSI
 */
public class PayFrame extends javax.swing.JInternalFrame {

    private BrandDAO brandDAO;
    private ProductDAO productDAO;
    private ProductDetailDAO productDetailDAO;
    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private BillDAO billDAO;
    private DeliveryDAO deliveryDAO;
    private ExchangeProductDAO exchangeProductDAO;

    private DefaultTableModel productTableModel;
    private DefaultTableModel productDetailTableModel;
    private DefaultTableModel orderDetailTableModel;
    private DefaultTableModel billTableModel;
    private DefaultTableModel billDetailTableModel;

    private JFileChooser chooser;

    private List<Product> listProduct1;
    private List<ProductDetail> listProductDetails1;
    private Map<Integer, Brand> mapBrand;
    private List<OrderDetail> listOrderDetail1;
    private List<Object[]> listOrderDetail2;

    private List<Object[]> listBill3;

    private final String[] cboOrderByFilterModel = new String[]{"B.ID", "B.CREATED_TIME", "SUM(OD.QUANTITY)", "SUM(OD.QUANTITY*OD.PRICE)"};
    private final String[] cboOrderDirectionModel = new String[]{"DESC", "ASC"};

    private Object[] selectedBill2;
    private Delivery selectedDelivery2;
    private ExchangeProduct selectedExchangeProduct2;

    public PayFrame() {
        initComponents();
        init();
    }

    private void init() {
        brandDAO = new BrandDAO();
        productDAO = new ProductDAO();
        productDetailDAO = new ProductDetailDAO();
        orderDAO = new OrderDAO();
        orderDetailDAO = new OrderDetailDAO();
        billDAO = new BillDAO();
        deliveryDAO = new DeliveryDAO();
        exchangeProductDAO = new ExchangeProductDAO();

        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        Font font = new Font("Tahoma", 1, 16);
        tblBill.getTableHeader().setFont(font);
        tblBillDetail.getTableHeader().setFont(font);
        tblOrderDetail.getTableHeader().setFont(font);
        tblProduct.getTableHeader().setFont(font);
        tblProductDetail.getTableHeader().setFont(font);

        listOrderDetail1 = new ArrayList<OrderDetail>();

        productTableModel = (DefaultTableModel) tblProduct.getModel();
        productDetailTableModel = (DefaultTableModel) tblProductDetail.getModel();
        orderDetailTableModel = (DefaultTableModel) tblOrderDetail.getModel();
        billTableModel = (DefaultTableModel) tblBill.getModel();
        billDetailTableModel = (DefaultTableModel) tblBillDetail.getModel();

        loadComboBoxHang();

        for (PaymentMethod value : PaymentMethod.values()) {
            cboPaymentMethodFilter.addItem(value.getValue());
        }

        tblBill.getSelectionModel().addListSelectionListener((e) -> {
            updateIndexStatus();
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItemRemove = new JMenuItem("Loại Khỏi Giỏ Hàng");
        popupMenu.add(menuItemRemove);
        tblOrderDetail.setComponentPopupMenu(popupMenu);
        menuItemRemove.addActionListener((e) -> {
            removeFromCart(tblOrderDetail.getSelectedRow());
        });

        btnShowDelivery.setVisible(false);
        btnExchangeProduct.setVisible(false);
        btnExchangeDetail.setVisible(false);
        btnExportPDF.setVisible(false);

        listProduct1 = productDAO.selectBySQL("SELECT * FROM PRODUCT WHERE ACTIVATE = 1");
        fillToTableProduct(listProduct1);
        resetAndFillToBillTable();
    }

    //Tab1
    private void clearPay() {
        listOrderDetail1.clear();
        orderDetailTableModel.setRowCount(0);
        lblTotal.setText("0 đ");
        rdoCash.setSelected(true);
        listProduct1 = productDAO.selectBySQL("SELECT * FROM PRODUCT WHERE ACTIVATE = 1");
        fillToTableProduct(listProduct1);
        productDetailTableModel.setRowCount(0);
    }

    private void removeFromCart(int index) {
        listOrderDetail1.remove(index);
        orderDetailTableModel.removeRow(index);
    }

    private void loadComboBoxHang() {
        DefaultComboBoxModel<Brand> comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.addElement(new Brand(0, "Tất Cả"));
        mapBrand = brandDAO.selectAll().stream().collect(Collectors.toMap(Brand::getId, Function.identity()));
        for (Brand brand : mapBrand.values()) {
            comboBoxModel.addElement(brand);
        }
        cboBrand.setModel(comboBoxModel);
    }

    private void fillToTableProduct(Product product) {
        productTableModel.addRow(new Object[]{product.getId(), product.getName(), mapBrand.get(product.getBrandID()).getName()});
    }

    private void fillToTableProduct(List<Product> list) {
        productTableModel.setRowCount(0);
        for (Product product : list) {
            this.fillToTableProduct(product);
        }
        txtSoLuong.setText("1");
    }

    private void fillToTableProductDetail(ProductDetail p) {
        productDetailTableModel.addRow(new Object[]{p.getType(), p.getSize(), MoneyFormat.formatVND(p.getPrice()), p.getBarcode(), p.getQuantity()});
    }

    private void fillToTableProductDetail(List<ProductDetail> list) {
        productDetailTableModel.setRowCount(0);
        for (ProductDetail productDetail : list) {
            fillToTableProductDetail(productDetail);
        }
        txtSoLuong.setText("1");
        if (tblProductDetail.getRowCount() > 0) {
            tblProductDetail.setRowSelectionInterval(0, 0);
        }
    }

    private void fillToTableOrderDetail(Object[] obj) {
        orderDetailTableModel.addRow(new Object[]{obj[0], obj[1], obj[2], obj[3], obj[4], MoneyFormat.formatVND(obj[5]), obj[6], MoneyFormat.formatVND(obj[7])});
    }

    private void fillToTableOrderDetail(int index, Object[] obj) {
        orderDetailTableModel.setValueAt(obj[0], index, 0);
        orderDetailTableModel.setValueAt(obj[1], index, 1);
        orderDetailTableModel.setValueAt(obj[2], index, 2);
        orderDetailTableModel.setValueAt(obj[3], index, 3);
        orderDetailTableModel.setValueAt(obj[4], index, 4);
        orderDetailTableModel.setValueAt(MoneyFormat.formatVND(obj[5]), index, 5);
        orderDetailTableModel.setValueAt(obj[6], index, 6);
        orderDetailTableModel.setValueAt(MoneyFormat.formatVND(obj[7]), index, 7);
    }

    private void calculateTotal() {
        double count = 0;
        for (OrderDetail orderDetail : listOrderDetail1) {
            count += orderDetail.getQuantity() * orderDetail.getPrice();
        }
        lblTotal.setText(MoneyFormat.formatVND(count));
    }

    //Tab2
    private void showBillDetail(int billID) {
        String sql = "SELECT B.ID, B.CREATED_TIME, B.PAYMENT_METHOD, B.EMPLOYEE_ID, B.ORDER_ID, E.FULLNAME, SUM(OD.QUANTITY), SUM(OD.QUANTITY*OD.PRICE) FROM BILL B INNER JOIN ORDERS O ON B.ORDER_ID = O.ID INNER JOIN EMPLOYEE E ON B.EMPLOYEE_ID = E.ID INNER JOIN ORDER_DETAIL OD ON O.ID = OD.ORDER_ID WHERE B.ID = ? GROUP BY B.ID, B.CREATED_TIME, B.PAYMENT_METHOD, B.EMPLOYEE_ID, E.FULLNAME, B.ORDER_ID";
        List<Object[]> list = JdbcHelper.getListOfArray(sql, 8, billID);
        if (list.isEmpty()) {
            MsgBox.alert(null, "Không tìm thấy thông tin hóa đơn !");
            return;
        }
        selectedBill2 = list.get(0);
        selectedDelivery2 = deliveryDAO.selectByOrderID((int) selectedBill2[4]);
        selectedExchangeProduct2 = exchangeProductDAO.selectByBillID((int) selectedBill2[0]);
        btnExportPDF.setVisible(true);
        btnShowDelivery.setVisible(selectedDelivery2 != null);
        btnExchangeProduct.setVisible(selectedExchangeProduct2 == null);
        btnExchangeDetail.setVisible(selectedExchangeProduct2 != null);

        lblBillID.setText(String.valueOf(selectedBill2[0]));
        lblBillCreatedTime.setText(DateHelper.toString((Timestamp) selectedBill2[1]));
        lblEmployeeID.setText(String.valueOf(selectedBill2[3]));
        lblEmployeeName.setText(String.valueOf(selectedBill2[5]));
        lblHinhThucGiaoDich.setText(selectedDelivery2 == null ? "Bán Tại Shop" : "Giao Hàng");
        lblPaymentMethod.setText(PaymentMethod.valueOf(String.valueOf(selectedBill2[2])).getValue());
        lblTotalQuantity.setText(String.valueOf(selectedBill2[6]));
        lblTotalMoney.setText(MoneyFormat.formatVND(selectedBill2[7]));

        String orderDetailSQL = "SELECT P.ID, P.NAME, P.BRAND_ID, PD.TYPE, PD.SIZE, OD.PRICE, OD.QUANTITY, OD.QUANTITY * OD.PRICE, OD.ID FROM ORDER_DETAIL OD INNER JOIN PRODUCT_DETAIL PD ON OD.PRODUCT_DETAIL_ID = PD.ID INNER JOIN PRODUCT P ON PD.PRODUCT_ID = P.ID WHERE ORDER_ID = ?";
        billDetailTableModel.setRowCount(0);
        listOrderDetail2 = JdbcHelper.getListOfArray(orderDetailSQL, 9, selectedBill2[4]);
        for (Object[] obj : listOrderDetail2) {
            billDetailTableModel.addRow(new Object[]{obj[0], obj[1], mapBrand.get(obj[2]), obj[3], obj[4], MoneyFormat.formatVND(obj[5]), obj[6], MoneyFormat.formatVND(obj[7])});
        }
    }

    //Tab3
    private void updateIndexStatus() {
        lblIndex.setText((tblBill.getSelectedRow() + 1) + " / " + listBill3.size());
    }

    private void resetAndFillToBillTable() {
        cboOrderByFilter.setSelectedIndex(0);
        cboPaymentMethodFilter.setSelectedIndex(0);
        cboOrderDirection.setSelectedIndex(0);

        String sql = "SELECT B.ID, B.CREATED_TIME, B.PAYMENT_METHOD, B.EMPLOYEE_ID, B.ORDER_ID, E.FULLNAME, SUM(OD.QUANTITY), SUM(OD.QUANTITY*OD.PRICE) FROM BILL B INNER JOIN ORDERS O ON B.ORDER_ID = O.ID INNER JOIN EMPLOYEE E ON B.EMPLOYEE_ID = E.ID INNER JOIN ORDER_DETAIL OD ON O.ID = OD.ORDER_ID GROUP BY B.ID, B.CREATED_TIME, B.PAYMENT_METHOD, B.EMPLOYEE_ID, E.FULLNAME, B.ORDER_ID ORDER BY B.ID DESC";

        listBill3 = JdbcHelper.getListOfArray(sql, 8);
        fillToTableBill(listBill3);
    }

    private void fillToTableBill(Object[] obj) {
        billTableModel.addRow(new Object[]{obj[0], obj[3], obj[5], DateHelper.toString((Timestamp) obj[1]), obj[6], MoneyFormat.formatVND(obj[7]), PaymentMethod.valueOf(String.valueOf(obj[2])).getValue()});
    }

    private void fillToTableBill(List<Object[]> list) {
        billTableModel.setRowCount(0);
        for (Object[] objects : list) {
            fillToTableBill(objects);
        }
        updateIndexStatus();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        tabPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cboBrand = new javax.swing.JComboBox<>();
        btnSearchProduct = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProduct = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProductDetail = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        btnMinus = new javax.swing.JButton();
        txtSoLuong = new javax.swing.JTextField();
        btnPlus = new javax.swing.JButton();
        btnAddToOrder = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblOrderDetail = new javax.swing.JTable();
        btnThanhToan = new javax.swing.JButton();
        btnGiaoHang = new javax.swing.JButton();
        btnReset1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        btnScanBarcode = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        rdoCash = new javax.swing.JRadioButton();
        rdoBanking = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblBillDetail = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        lblTotalQuantityTitle = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        lblBillID = new javax.swing.JLabel();
        lblBillCreatedTime = new javax.swing.JLabel();
        lblEmployeeID = new javax.swing.JLabel();
        lblEmployeeName = new javax.swing.JLabel();
        lblPaymentMethod = new javax.swing.JLabel();
        lblTotalQuantity = new javax.swing.JLabel();
        lblTotalMoney = new javax.swing.JLabel();
        lblPaymentMethodTitle = new javax.swing.JLabel();
        lblHinhThucGiaoDich = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        btnExportPDF = new javax.swing.JButton();
        btnExchangeDetail = new javax.swing.JButton();
        btnExchangeProduct = new javax.swing.JButton();
        btnShowDelivery = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtSearchBill = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnShowAll = new javax.swing.JButton();
        cboPaymentMethodFilter = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cboOrderByFilter = new javax.swing.JComboBox<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblBill = new javax.swing.JTable();
        cboOrderDirection = new javax.swing.JComboBox<>();
        lblIndex = new javax.swing.JLabel();
        btnScanQRCode = new javax.swing.JButton();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        txtSearch1 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        cboBrand1 = new javax.swing.JComboBox<>();
        btnSearchProduct1 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblProduct1 = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblProductDetail1 = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        btnMinus1 = new javax.swing.JButton();
        txtSoLuong1 = new javax.swing.JTextField();
        btnPlus1 = new javax.swing.JButton();
        btnAddToOrder1 = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblOrderDetail1 = new javax.swing.JTable();
        btnThanhToan1 = new javax.swing.JButton();
        btnGiaoHang1 = new javax.swing.JButton();
        btnReset2 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        lblTotal1 = new javax.swing.JLabel();
        btnScanBarcode1 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jComboBox5 = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox<>();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setTitle("ShoeStoreSys - Thanh Toán");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logoExtraSmall.jpg"))); // NOI18N
        setPreferredSize(new java.awt.Dimension(1200, 800));

        tabPane.setFocusable(false);
        tabPane.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Hãng");

        cboBrand.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboBrand.setFocusable(false);

        btnSearchProduct.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnSearchProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/search-30.png"))); // NOI18N
        btnSearchProduct.setText("Tìm Kiếm");
        btnSearchProduct.setFocusable(false);
        btnSearchProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchProductActionPerformed(evt);
            }
        });

        tblProduct.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã", "Tên", "Hãng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProduct.setRowHeight(30);
        tblProduct.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblProduct.getTableHeader().setReorderingAllowed(false);
        tblProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblProductMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblProduct);

        tblProductDetail.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblProductDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Loại", "Cỡ", "Giá", "Mã Vạch", "Số Lượng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProductDetail.setRowHeight(30);
        tblProductDetail.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblProductDetail.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblProductDetail);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Số Lượng ");

        btnMinus.setText("-");
        btnMinus.setFocusable(false);
        btnMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMinusActionPerformed(evt);
            }
        });

        txtSoLuong.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtSoLuong.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSoLuong.setText("1");
        txtSoLuong.setEnabled(false);

        btnPlus.setText("+");
        btnPlus.setFocusable(false);
        btnPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlusActionPerformed(evt);
            }
        });

        btnAddToOrder.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnAddToOrder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shopping-basket-add-30.png"))); // NOI18N
        btnAddToOrder.setText("Thêm Vào Đơn Hàng");
        btnAddToOrder.setFocusable(false);
        btnAddToOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToOrderActionPerformed(evt);
            }
        });

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
        tblOrderDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblOrderDetailMousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(tblOrderDetail);
        if (tblOrderDetail.getColumnModel().getColumnCount() > 0) {
            tblOrderDetail.getColumnModel().getColumn(0).setResizable(false);
        }

        btnThanhToan.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnThanhToan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/money-30.png"))); // NOI18N
        btnThanhToan.setText("Thanh Toán");
        btnThanhToan.setFocusable(false);
        btnThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThanhToanActionPerformed(evt);
            }
        });

        btnGiaoHang.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnGiaoHang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/delivery-scooter-30.png"))); // NOI18N
        btnGiaoHang.setText("Giao Hàng");
        btnGiaoHang.setFocusable(false);
        btnGiaoHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGiaoHangActionPerformed(evt);
            }
        });

        btnReset1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnReset1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/refresh-25.png"))); // NOI18N
        btnReset1.setText("Làm Mới");
        btnReset1.setFocusable(false);
        btnReset1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReset1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Tổng Tiền Thanh Toán:");

        lblTotal.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(255, 51, 51));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal.setText("0 VNĐ");

        btnScanBarcode.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnScanBarcode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/scanner.png"))); // NOI18N
        btnScanBarcode.setText("Quét Mã");
        btnScanBarcode.setFocusable(false);
        btnScanBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanBarcodeActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel4.setText("Tìm Kiếm");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Phương thức thanh toán");

        buttonGroup1.add(rdoCash);
        rdoCash.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        rdoCash.setForeground(new java.awt.Color(0, 153, 255));
        rdoCash.setSelected(true);
        rdoCash.setText("Tiền Mặt");
        rdoCash.setFocusable(false);

        buttonGroup1.add(rdoBanking);
        rdoBanking.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        rdoBanking.setForeground(new java.awt.Color(0, 153, 255));
        rdoBanking.setText("Chuyển Khoản / Thẻ");
        rdoBanking.setFocusable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 562, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                .addComponent(btnSearchProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnScanBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(309, 309, 309)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddToOrder))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGiaoHang, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnReset1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8)
                        .addGap(50, 50, 50)
                        .addComponent(rdoCash)
                        .addGap(18, 18, 18)
                        .addComponent(rdoBanking)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnScanBarcode, btnSearchProduct});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGiaoHang, btnReset1, btnThanhToan});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboBrand)
                    .addComponent(btnSearchProduct, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .addComponent(btnScanBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnPlus)
                        .addComponent(btnMinus)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAddToOrder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(txtSoLuong, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(rdoCash)
                    .addComponent(rdoBanking)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGiaoHang, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnSearchProduct, cboBrand, jLabel1, txtSearch});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAddToOrder, jLabel2});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGiaoHang, btnReset1, btnThanhToan});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnMinus, btnPlus, txtSoLuong});

        tabPane.addTab("Thanh Toán", jPanel1);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 153, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("THÔNG TIN HÓA ĐƠN");

        tblBillDetail.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblBillDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã ", "Tên", "Hãng", "Loại", "Cỡ", "Giá", "Số Lượng", "Tiền Hàng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBillDetail.setRowHeight(30);
        tblBillDetail.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblBillDetail.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(tblBillDetail);

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(68, 68, 68));
        jLabel18.setText("Mã Đơn");

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(68, 68, 68));
        jLabel22.setText("Thời Gian Tạo");

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(68, 68, 68));
        jLabel23.setText("Mã Nhân Viên");

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(68, 68, 68));
        jLabel25.setText("Tên Nhân Viên");

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(68, 68, 68));
        jLabel27.setText("Hình Thức Giao Dịch");

        lblTotalQuantityTitle.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblTotalQuantityTitle.setForeground(new java.awt.Color(68, 68, 68));
        lblTotalQuantityTitle.setText("Tổng Số Sản Phẩm");

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(68, 68, 68));
        jLabel30.setText("Tổng Tiền Thanh Toán");
        jLabel30.setPreferredSize(new java.awt.Dimension(203, 33));

        lblBillID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblBillID.setForeground(new java.awt.Color(0, 51, 204));

        lblBillCreatedTime.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblBillCreatedTime.setForeground(new java.awt.Color(0, 51, 204));

        lblEmployeeID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblEmployeeID.setForeground(new java.awt.Color(0, 51, 204));

        lblEmployeeName.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblEmployeeName.setForeground(new java.awt.Color(0, 51, 204));

        lblPaymentMethod.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblPaymentMethod.setForeground(new java.awt.Color(0, 51, 204));

        lblTotalQuantity.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblTotalQuantity.setForeground(new java.awt.Color(0, 51, 204));

        lblTotalMoney.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblTotalMoney.setForeground(new java.awt.Color(0, 51, 204));

        lblPaymentMethodTitle.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblPaymentMethodTitle.setForeground(new java.awt.Color(68, 68, 68));
        lblPaymentMethodTitle.setText("Phương Thức Thanh Toán");

        lblHinhThucGiaoDich.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblHinhThucGiaoDich.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 13, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 72, Short.MAX_VALUE)
        );

        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 20, 5);
        flowLayout1.setAlignOnBaseline(true);
        jPanel8.setLayout(flowLayout1);

        btnExportPDF.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnExportPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/export_pdf_30.png"))); // NOI18N
        btnExportPDF.setText("Xuất File PDF");
        btnExportPDF.setFocusable(false);
        btnExportPDF.setPreferredSize(new java.awt.Dimension(250, 50));
        btnExportPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportPDFActionPerformed(evt);
            }
        });
        jPanel8.add(btnExportPDF);

        btnExchangeDetail.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnExchangeDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/refresh-25.png"))); // NOI18N
        btnExchangeDetail.setText("Thông Tin Đổi Hàng");
        btnExchangeDetail.setFocusable(false);
        btnExchangeDetail.setPreferredSize(new java.awt.Dimension(250, 50));
        btnExchangeDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExchangeDetailActionPerformed(evt);
            }
        });
        jPanel8.add(btnExchangeDetail);

        btnExchangeProduct.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnExchangeProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/refresh-25.png"))); // NOI18N
        btnExchangeProduct.setText("Yêu Cầu Đổi Hàng");
        btnExchangeProduct.setFocusable(false);
        btnExchangeProduct.setPreferredSize(new java.awt.Dimension(250, 50));
        btnExchangeProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExchangeProductActionPerformed(evt);
            }
        });
        jPanel8.add(btnExchangeProduct);

        btnShowDelivery.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnShowDelivery.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/search-30.png"))); // NOI18N
        btnShowDelivery.setText("Xem Đơn Giao Hàng");
        btnShowDelivery.setFocusable(false);
        btnShowDelivery.setPreferredSize(new java.awt.Dimension(250, 50));
        btnShowDelivery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowDeliveryActionPerformed(evt);
            }
        });
        jPanel8.add(btnShowDelivery);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25)
                            .addComponent(jLabel18)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEmployeeName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblEmployeeID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblBillCreatedTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblBillID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27)
                            .addComponent(lblTotalQuantityTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPaymentMethodTitle)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(58, 58, 58)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblTotalQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblTotalMoney, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblHinhThucGiaoDich, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPaymentMethod, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel18, jLabel22, jLabel23, jLabel25, jLabel30});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblHinhThucGiaoDich, lblPaymentMethod, lblTotalMoney, lblTotalQuantity});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel22)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel23)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel25))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel27)
                                    .addComponent(lblHinhThucGiaoDich))
                                .addGap(18, 18, 18)
                                .addComponent(lblPaymentMethodTitle)
                                .addGap(18, 18, 18)
                                .addComponent(lblTotalQuantityTitle)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addComponent(lblPaymentMethod)
                            .addGap(18, 18, 18)
                            .addComponent(lblTotalQuantity)
                            .addGap(18, 18, 18)
                            .addComponent(lblTotalMoney)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(153, 153, 153)
                        .addComponent(lblEmployeeName))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblBillID)
                        .addGap(18, 18, 18)
                        .addComponent(lblBillCreatedTime)
                        .addGap(18, 18, 18)
                        .addComponent(lblEmployeeID)))
                .addGap(13, 13, 13)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel18, jLabel22, jLabel23, jLabel25, jLabel27, jLabel30, lblBillCreatedTime, lblBillID, lblEmployeeID, lblEmployeeName, lblHinhThucGiaoDich, lblPaymentMethod, lblPaymentMethodTitle, lblTotalMoney, lblTotalQuantity, lblTotalQuantityTitle});

        tabPane.addTab("Thông Tin Hóa Đơn", jPanel2);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Mã Hóa Đơn");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Phương Thức Thanh Toán");

        txtSearchBill.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

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
        btnShowAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/refresh-25.png"))); // NOI18N
        btnShowAll.setText("Hiển Thị Tất Cả");
        btnShowAll.setFocusable(false);
        btnShowAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowAllActionPerformed(evt);
            }
        });

        cboPaymentMethodFilter.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboPaymentMethodFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất Cả" }));
        cboPaymentMethodFilter.setFocusable(false);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Sắp Xếp Theo");

        cboOrderByFilter.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboOrderByFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mã Hóa Đơn", "Thời Gian Tạo", "Số Lượng Hàng", "Tổng Giá" }));
        cboOrderByFilter.setFocusable(false);

        tblBill.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblBill.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Hóa Đơn", "Mã Nhân Viên", "Tên Nhân Viên", "Thời Gian Tạo", "Số Lượng Sản Phẩm", "Tổng Tiền Thanh Toán", "Phương Thức Thanh Toán"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBill.setRowHeight(30);
        tblBill.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblBill.getTableHeader().setReorderingAllowed(false);
        tblBill.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblBillMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(tblBill);

        cboOrderDirection.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboOrderDirection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Giảm Dần", "Tăng Dần" }));
        cboOrderDirection.setFocusable(false);

        lblIndex.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblIndex.setForeground(new java.awt.Color(255, 102, 102));
        lblIndex.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIndex.setText("Index");
        lblIndex.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        btnScanQRCode.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnScanQRCode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/scanner.png"))); // NOI18N
        btnScanQRCode.setText("Quét Mã Hóa Đơn");
        btnScanQRCode.setFocusable(false);
        btnScanQRCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanQRCodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtSearchBill, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(cboPaymentMethodFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(cboOrderByFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(cboOrderDirection, 0, 135, Short.MAX_VALUE)
                                .addGap(83, 83, 83))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnShowAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnScanQRCode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearchBill)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnScanQRCode))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                    .addComponent(cboPaymentMethodFilter)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                    .addComponent(cboOrderByFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboOrderDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnShowAll, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(lblIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnScanQRCode, btnSearch, btnShowAll, jLabel5, txtSearchBill});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboPaymentMethodFilter, jLabel6, jLabel7});

        tabPane.addTab("Danh Sách", jPanel3);

        jInternalFrame1.setClosable(true);
        jInternalFrame1.setIconifiable(true);
        jInternalFrame1.setMaximizable(true);
        jInternalFrame1.setTitle("Thanh Toán");
        jInternalFrame1.setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logoExtraSmall.jpg"))); // NOI18N
        jInternalFrame1.setPreferredSize(new java.awt.Dimension(1200, 800));

        jTabbedPane4.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        txtSearch1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Hãng");

        cboBrand1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboBrand1.setFocusable(false);
        cboBrand1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboBrand1ItemStateChanged(evt);
            }
        });
        cboBrand1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBrand1ActionPerformed(evt);
            }
        });

        btnSearchProduct1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnSearchProduct1.setText("Tìm Kiếm");
        btnSearchProduct1.setFocusable(false);
        btnSearchProduct1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchProduct1ActionPerformed(evt);
            }
        });

        tblProduct1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblProduct1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã", "Tên", "Hãng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProduct1.setRowHeight(30);
        tblProduct1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblProduct1.getTableHeader().setReorderingAllowed(false);
        tblProduct1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblProduct1MouseReleased(evt);
            }
        });
        jScrollPane5.setViewportView(tblProduct1);

        tblProductDetail1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblProductDetail1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Loại", "Cỡ", "Giá", "Mã Vạch", "Số Lượng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProductDetail1.setRowHeight(30);
        tblProductDetail1.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(tblProductDetail1);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Số Lượng ");

        btnMinus1.setText("-");
        btnMinus1.setFocusable(false);
        btnMinus1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMinus1ActionPerformed(evt);
            }
        });

        txtSoLuong1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtSoLuong1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSoLuong1.setText("1");
        txtSoLuong1.setEnabled(false);

        btnPlus1.setText("+");
        btnPlus1.setFocusable(false);
        btnPlus1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlus1ActionPerformed(evt);
            }
        });

        btnAddToOrder1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnAddToOrder1.setText("Thêm Vào Đơn Hàng");
        btnAddToOrder1.setFocusable(false);
        btnAddToOrder1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToOrder1ActionPerformed(evt);
            }
        });

        tblOrderDetail1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblOrderDetail1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã ", "Tên", "Hãng", "Loại", "Cỡ", "Giá", "Số Lượng", "Tiền Hàng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblOrderDetail1.setRowHeight(30);
        tblOrderDetail1.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(tblOrderDetail1);

        btnThanhToan1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnThanhToan1.setText("Thanh Toán");
        btnThanhToan1.setFocusable(false);
        btnThanhToan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThanhToan1ActionPerformed(evt);
            }
        });

        btnGiaoHang1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnGiaoHang1.setText("Giao Hàng");
        btnGiaoHang1.setFocusable(false);

        btnReset2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnReset2.setText("Làm Mới");
        btnReset2.setFocusable(false);
        btnReset2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReset2ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Tổng Tiền :");

        lblTotal1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblTotal1.setForeground(new java.awt.Color(255, 51, 51));
        lblTotal1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal1.setText("0 đ");

        btnScanBarcode1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnScanBarcode1.setText("Quét Mã");
        btnScanBarcode1.setFocusable(false);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel12.setText("Tìm Kiếm");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Phương thức thanh toán");

        jRadioButton3.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jRadioButton3.setText("Tiền Mặt");

        jRadioButton4.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jRadioButton4.setText("Chuyển Khoản / Thẻ");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 562, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboBrand1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSearchProduct1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnScanBarcode1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane6)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(309, 309, 309)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMinus1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSoLuong1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPlus1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddToOrder1))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane7))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnThanhToan1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGiaoHang1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnReset2, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addGap(70, 70, 70)
                .addComponent(jRadioButton3)
                .addGap(18, 18, 18)
                .addComponent(jRadioButton4)
                .addGap(227, 227, 227)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotal1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch1)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboBrand1)
                    .addComponent(btnSearchProduct1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnScanBarcode1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSoLuong1)
                    .addComponent(btnPlus1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMinus1)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddToOrder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton4)
                    .addComponent(jLabel11)
                    .addComponent(lblTotal1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThanhToan1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGiaoHang1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jTabbedPane4.addTab("Thanh Toán", jPanel4);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jTabbedPane4.addTab("Thông Tin Hóa Đơn", jPanel5);

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Mã Hóa Đơn");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Trạng Thái");

        jButton10.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton10.setText("Tìm Kiếm");

        jButton11.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton11.setText("Hiển Thị Tất Cả");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Sắp Xếp Theo");

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Hóa Đơn", "Nhân Viên Tạo", "Thời Gian Tạo", "Số Lượng Hàng", "Tổng Giá", "Trạng Thái"
            }
        ));
        jScrollPane8.setViewportView(jTable5);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox6, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 653, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField4)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                        .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox5)
                        .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                        .addComponent(jComboBox6, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("Danh Sách", jPanel6);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addComponent(jTabbedPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 592, Short.MAX_VALUE)
                    .addComponent(jInternalFrame1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 592, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabPane)
                .addGap(0, 0, 0))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 382, Short.MAX_VALUE)
                    .addComponent(jInternalFrame1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 382, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchProductActionPerformed
        // TODO add your handling code here:
        String sql = "SELECT * FROM PRODUCT WHERE ACTIVATE = 1 AND ( ID LIKE ? OR NAME LIKE ? )";
        String search = "%" + txtSearch.getText() + "%";

        if (cboBrand.getSelectedIndex() > 0) {
            sql += " AND BRAND_ID = ?";
            listProduct1 = productDAO.selectBySQL(sql, search, search, ((Brand) cboBrand.getSelectedItem()).getId());
        } else {
            listProduct1 = productDAO.selectBySQL(sql, search, search);
        }
        fillToTableProduct(listProduct1);
        productDetailTableModel.setRowCount(0);
        tblProductDetail.clearSelection();

    }//GEN-LAST:event_btnSearchProductActionPerformed

    private void btnPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlusActionPerformed
        int max = listProductDetails1.get(tblProductDetail.getSelectedRow()).getQuantity();
        int values = Integer.parseInt(this.txtSoLuong.getText());
        if (values < max) {
            this.txtSoLuong.setText(String.valueOf(values + 1));
        }
    }//GEN-LAST:event_btnPlusActionPerformed

    private void btnAddToOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToOrderActionPerformed
        int index = this.tblProduct.getSelectedRow();
        int indexProductDetail = this.tblProductDetail.getSelectedRow();
        if (index == -1 || indexProductDetail == -1) {
            return;
        }

        int quantity = Integer.parseInt(txtSoLuong.getText());

        Product product = listProduct1.get(index);
        ProductDetail productDetail = listProductDetails1.get(indexProductDetail);
        Object[] obj = new Object[]{product.getId(), product.getName(), mapBrand.get(product.getBrandID()), productDetail.getType().toString(), productDetail.getSize(), productDetail.getPrice(), quantity, productDetail.getPrice() * quantity};

        for (int i = 0; i < listOrderDetail1.size(); i++) {
            if (productDetail.getId() == listOrderDetail1.get(i).getProductDetailID()) {
                listProductDetails1.set(i, productDetail);
                fillToTableOrderDetail(i, obj);
                calculateTotal();
                return;
            }
        }

        listOrderDetail1.add(new OrderDetail(quantity, productDetail.getPrice(), productDetail.getId(), 0));
        fillToTableOrderDetail(obj);
        calculateTotal();
    }//GEN-LAST:event_btnAddToOrderActionPerformed

    private void btnMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMinusActionPerformed
        int value = Integer.parseInt(this.txtSoLuong.getText());
        if (value > 1) {
            txtSoLuong.setText(String.valueOf(--value));
        }
    }//GEN-LAST:event_btnMinusActionPerformed

    private void btnReset1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReset1ActionPerformed
        clearPay();
    }//GEN-LAST:event_btnReset1ActionPerformed

    private void tblProductMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductMouseReleased
        // TODO add your handling code here:
        int index = tblProduct.getSelectedRow();
        if (index == -1) {
            return;
        }
        String sql = "SELECT * FROM PRODUCT_DETAIL WHERE PRODUCT_ID = ? AND QUANTITY > 0";
        listProductDetails1 = productDetailDAO.selectBySQL(sql, listProduct1.get(index).getId());
        fillToTableProductDetail(listProductDetails1);
    }//GEN-LAST:event_tblProductMouseReleased

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
        if (listOrderDetail1.size() == 0) {
            MsgBox.alert(null, "Chưa có mặt hàng nào trong giỏ");
            return;
        }

        if (MsgBox.confirm(null, "Xác nhận thanh toán ?")) {
            Timestamp createdTime = new Timestamp(DateHelper.now().getTime());
            Order order = new Order(createdTime, Auth.getUser().getId());
            orderDAO.insert(order);
            for (OrderDetail orderDetail : listOrderDetail1) {
                orderDetail.setOrderID(order.getId());
                orderDetailDAO.insert(orderDetail);
                productDetailDAO.updateQuantityByID((orderDetail.getQuantity() * -1), orderDetail.getProductDetailID());
            }
            Bill bill = new Bill(createdTime, rdoCash.isSelected() ? PaymentMethod.CASH : PaymentMethod.BANKING, Auth.getUser().getId(), order.getId());
            billDAO.insert(bill);
            clearPay();
            MsgBox.alert(null, "Tạo hóa đơn thành công !");
            showBillDetail(bill.getId());
            tabPane.setSelectedIndex(1);
            resetAndFillToBillTable();
        }
    }//GEN-LAST:event_btnThanhToanActionPerformed

    private void cboBrand1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboBrand1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cboBrand1ItemStateChanged

    private void cboBrand1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBrand1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboBrand1ActionPerformed

    private void btnSearchProduct1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchProduct1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSearchProduct1ActionPerformed

    private void tblProduct1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProduct1MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblProduct1MouseReleased

    private void btnMinus1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMinus1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMinus1ActionPerformed

    private void btnPlus1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlus1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPlus1ActionPerformed

    private void btnAddToOrder1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToOrder1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddToOrder1ActionPerformed

    private void btnThanhToan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToan1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnThanhToan1ActionPerformed

    private void btnReset2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReset2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnReset2ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton11ActionPerformed

    private void btnShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowAllActionPerformed
        resetAndFillToBillTable();
    }//GEN-LAST:event_btnShowAllActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String search = txtSearchBill.getText();
        try {
            Integer.parseInt(search);
        } catch (NumberFormatException e) {
            MsgBox.alert(null, "Không tìm thấy hóa đơn !");
            return;
        }
        String prefix = "SELECT B.ID, B.CREATED_TIME, B.PAYMENT_METHOD, B.EMPLOYEE_ID, B.ORDER_ID, E.FULLNAME, SUM(OD.QUANTITY), SUM(OD.QUANTITY*OD.PRICE) FROM BILL B INNER JOIN ORDERS O ON B.ORDER_ID = O.ID INNER JOIN EMPLOYEE E ON B.EMPLOYEE_ID = E.ID INNER JOIN ORDER_DETAIL OD ON O.ID = OD.ORDER_ID";
        String suffic = " GROUP BY B.ID, B.CREATED_TIME, B.PAYMENT_METHOD, B.EMPLOYEE_ID, E.FULLNAME, B.ORDER_ID";
        String orderBy = " ORDER BY " + cboOrderByFilterModel[cboOrderByFilter.getSelectedIndex()] + " " + cboOrderDirectionModel[cboOrderDirection.getSelectedIndex()];

        if (!search.isEmpty()) {
            prefix += " WHERE B.ID = " + search;
            if (cboPaymentMethodFilter.getSelectedIndex() > 0) {
                prefix += " AND B.PAYMENT_METHOD = '" + PaymentMethod.values()[cboPaymentMethodFilter.getSelectedIndex() - 1] + "'";
            }
        } else {
            if (cboPaymentMethodFilter.getSelectedIndex() > 0) {
                prefix += " WHERE B.PAYMENT_METHOD = '" + PaymentMethod.values()[cboPaymentMethodFilter.getSelectedIndex() - 1] + "'";
            }
        }

        listBill3 = JdbcHelper.getListOfArray(prefix + suffic + orderBy, 8);
        fillToTableBill(listBill3);
    }//GEN-LAST:event_btnSearchActionPerformed

    private void tblBillMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBillMouseReleased
        if (SwingUtilities.isLeftMouseButton(evt)) {
            if (evt.getClickCount() == 2) {
                showBillDetail((int) listBill3.get(tblBill.getSelectedRow())[0]);
                tabPane.setSelectedIndex(1);
            }
        }
    }//GEN-LAST:event_tblBillMouseReleased

    private void btnExchangeProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExchangeProductActionPerformed
        if (selectedBill2 == null) {
            MsgBox.alert(null, "Không có đơn hàng để đổi trả !");
            return;
        }

        if (DateHelper.addDays((Timestamp) selectedBill2[1], 7).before(new Date())) {
            MsgBox.alert(null, "Quá hạn 7 ngày để đổi trả !");
            return;
        }

        if (selectedExchangeProduct2 != null) {
            MsgBox.alert(null, "Đơn hàng chỉ được đổi trả 1 lần !");
            return;
        }

        int index = tblBillDetail.getSelectedRow();
        if (index == -1) {
            MsgBox.alert(null, "Vui lòng chọn sản phẩm bên dưới cần đổi trả !");
            return;
        }

        ExchangeProductDialog exchangeProductDialog = new ExchangeProductDialog(null, true, listOrderDetail2.get(index), orderDetailDAO.selectByID((int) listOrderDetail2.get(index)[8]), (int) selectedBill2[0]);
        exchangeProductDialog.setVisible(true);
        if (exchangeProductDialog.isSucceeded()) {
            showBillDetail((int) selectedBill2[0]);
            resetAndFillToBillTable();
        }
    }//GEN-LAST:event_btnExchangeProductActionPerformed

    private void btnGiaoHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGiaoHangActionPerformed
        if (listOrderDetail1.size() == 0) {
            MsgBox.alert(null, "Chưa có mặt hàng nào trong giỏ");
            return;
        }
        DeliveryDetailDialog deliveryDetailDialog = new DeliveryDetailDialog(null, true);
        deliveryDetailDialog.setVisible(true);
        Delivery delivery = deliveryDetailDialog.getDelivery();
        if (delivery != null) {
            Timestamp createdTime = new Timestamp(DateHelper.now().getTime());
            Order order = new Order(createdTime, Auth.getUser().getId());
            delivery.setUpdateTime(createdTime);
            orderDAO.insert(order);
            for (OrderDetail orderDetail : listOrderDetail1) {
                orderDetail.setOrderID(order.getId());
                orderDetailDAO.insert(orderDetail);
                productDetailDAO.updateQuantityByID((orderDetail.getQuantity() * -1), orderDetail.getProductDetailID());
            }
            delivery.setOrderID(order.getId());
            deliveryDAO.insert(delivery);
            if (rdoBanking.isSelected()) {
                Bill bill = new Bill(createdTime, PaymentMethod.BANKING, Auth.getUser().getId(), order.getId());
                billDAO.insert(bill);
                showBillDetail(bill.getId());
                tabPane.setSelectedIndex(1);
                resetAndFillToBillTable();
            }
            clearPay();
            MsgBox.alert(null, "Tạo đơn đặt hàng thành công !");
        }
    }//GEN-LAST:event_btnGiaoHangActionPerformed

    private void btnScanBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScanBarcodeActionPerformed
        ScanBarcodeDialog scanBarcodeDialog = new ScanBarcodeDialog(null, true);
        scanBarcodeDialog.setVisible(true);
        String barcode = scanBarcodeDialog.getResult();
        if (barcode == null) {
            return;
        }
        ProductDetail productDetail = productDetailDAO.selectByBarcode(barcode);
        if (productDetail == null) {
            MsgBox.alert(null, "Không tìm thấy sản phẩm !");
            return;
        }
        if (productDetail.getQuantity() < 1) {
            MsgBox.alert(null, "Sản phẩm đã hết hàng !");
            return;
        }

        Product product = productDAO.selectByID(productDetail.getProductID());
        if (!product.isActivate()) {
            MsgBox.alert(null, "Sản phẩm ngừng kinh doanh !");
            return;
        }

        listProduct1 = Collections.singletonList(product);
        fillToTableProduct(listProduct1);
        tblProduct.setRowSelectionInterval(0, 0);

        String sql = "SELECT * FROM PRODUCT_DETAIL WHERE PRODUCT_ID = ? AND QUANTITY > 0";
        listProductDetails1 = productDetailDAO.selectBySQL(sql, productDetail.getProductID());
        fillToTableProductDetail(listProductDetails1);

        for (int i = 0; i < listProductDetails1.size(); i++) {
            if (productDetail.getId() == listProductDetails1.get(i).getId()) {
                tblProductDetail.setRowSelectionInterval(i, i);
                return;
            }
        }
    }//GEN-LAST:event_btnScanBarcodeActionPerformed

    private void tblOrderDetailMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrderDetailMousePressed
        if (SwingUtilities.isRightMouseButton(evt)) {
            Point point = evt.getPoint();
            int index = tblOrderDetail.rowAtPoint(point);
            tblOrderDetail.setRowSelectionInterval(index, index);
        }
    }//GEN-LAST:event_tblOrderDetailMousePressed

    private void btnShowDeliveryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowDeliveryActionPerformed
        if (selectedDelivery2 == null) {
            MsgBox.alert(null, "Không có thông tin đơn giao để hiển thị !");
            return;
        }
        DeliveryDetailDialog deliveryDetailDialog = new DeliveryDetailDialog(null, true, selectedDelivery2);
        deliveryDetailDialog.setVisible(true);
    }//GEN-LAST:event_btnShowDeliveryActionPerformed

    private void btnExchangeDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExchangeDetailActionPerformed
        if (selectedExchangeProduct2 == null) {
            MsgBox.alert(null, "Không có thông tin đổi trả để hiển thị !");
            return;
        }
        ExchangeProductDetailDialog detailDialog = new ExchangeProductDetailDialog(null, true, selectedExchangeProduct2);
        detailDialog.setVisible(true);
    }//GEN-LAST:event_btnExchangeDetailActionPerformed

    private void btnExportPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportPDFActionPerformed
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = PDFHelper.exportOrderPDF(chooser.getSelectedFile().toString(), (int) selectedBill2[0], String.valueOf(selectedBill2[5]), DateHelper.toString((Timestamp) selectedBill2[1]), selectedDelivery2 == null ? "Bán Tại Shop" : "Giao Hàng", PaymentMethod.valueOf(selectedBill2[2].toString()).getValue(), listOrderDetail2, MoneyFormat.formatVND(selectedBill2[7]));
            if (path != null) {
                if (MsgBox.confirm(null, "Đã xuất hóa đơn, bạn có muốn mở ?")) {
                    try {
                        Desktop.getDesktop().open(new File(path));
                    } catch (IOException ex) {
                        MsgBox.alert(null, "Có lỗi xảy ra, hãy thử lại sau");
                    }
                }
            } else {
                MsgBox.alert(null, "Có lỗi xảy ra, hãy thử lại sau");
            }
        }
    }//GEN-LAST:event_btnExportPDFActionPerformed

    private void btnScanQRCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScanQRCodeActionPerformed
        ScanBarcodeDialog scanBarcodeDialog = new ScanBarcodeDialog(null, true);
        scanBarcodeDialog.setVisible(true);
        String rs = scanBarcodeDialog.getResult();
        if (rs == null) {
            return;
        }

        if (rs.startsWith("ShoeStoreSys-HoaDonSo")) {
            try {
                int billID = Integer.parseInt(rs.substring(21));
                showBillDetail(billID);
                tabPane.setSelectedIndex(1);
                tblBill.clearSelection();
                return;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        MsgBox.alert(null, "Không tìm thấy thông tin hóa đơn !");
    }//GEN-LAST:event_btnScanQRCodeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddToOrder;
    private javax.swing.JButton btnAddToOrder1;
    private javax.swing.JButton btnExchangeDetail;
    private javax.swing.JButton btnExchangeProduct;
    private javax.swing.JButton btnExportPDF;
    private javax.swing.JButton btnGiaoHang;
    private javax.swing.JButton btnGiaoHang1;
    private javax.swing.JButton btnMinus;
    private javax.swing.JButton btnMinus1;
    private javax.swing.JButton btnPlus;
    private javax.swing.JButton btnPlus1;
    private javax.swing.JButton btnReset1;
    private javax.swing.JButton btnReset2;
    private javax.swing.JButton btnScanBarcode;
    private javax.swing.JButton btnScanBarcode1;
    private javax.swing.JButton btnScanQRCode;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchProduct;
    private javax.swing.JButton btnSearchProduct1;
    private javax.swing.JButton btnShowAll;
    private javax.swing.JButton btnShowDelivery;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnThanhToan1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<Brand> cboBrand;
    private javax.swing.JComboBox<Brand> cboBrand1;
    private javax.swing.JComboBox<String> cboOrderByFilter;
    private javax.swing.JComboBox<String> cboOrderDirection;
    private javax.swing.JComboBox<String> cboPaymentMethodFilter;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JLabel lblBillCreatedTime;
    private javax.swing.JLabel lblBillID;
    private javax.swing.JLabel lblEmployeeID;
    private javax.swing.JLabel lblEmployeeName;
    private javax.swing.JLabel lblHinhThucGiaoDich;
    private javax.swing.JLabel lblIndex;
    private javax.swing.JLabel lblPaymentMethod;
    private javax.swing.JLabel lblPaymentMethodTitle;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotal1;
    private javax.swing.JLabel lblTotalMoney;
    private javax.swing.JLabel lblTotalQuantity;
    private javax.swing.JLabel lblTotalQuantityTitle;
    private javax.swing.JRadioButton rdoBanking;
    private javax.swing.JRadioButton rdoCash;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JTable tblBill;
    private javax.swing.JTable tblBillDetail;
    private javax.swing.JTable tblOrderDetail;
    private javax.swing.JTable tblOrderDetail1;
    private javax.swing.JTable tblProduct;
    private javax.swing.JTable tblProduct1;
    private javax.swing.JTable tblProductDetail;
    private javax.swing.JTable tblProductDetail1;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSearch1;
    private javax.swing.JTextField txtSearchBill;
    private javax.swing.JTextField txtSoLuong;
    private javax.swing.JTextField txtSoLuong1;
    // End of variables declaration//GEN-END:variables
}
