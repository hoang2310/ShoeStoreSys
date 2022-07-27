/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro1041.ui;

import java.awt.Point;
import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import pro1041.dao.BrandDAO;
import pro1041.dao.ProductDAO;
import pro1041.dao.ProductDetailDAO;
import pro1041.entity.Brand;
import pro1041.entity.Product;
import pro1041.entity.ProductDetail;
import pro1041.entity.Type;
import pro1041.utils.Auth;
import pro1041.utils.DateHelper;
import pro1041.utils.ImageHelper;
import pro1041.utils.JdbcHelper;
import pro1041.utils.MoneyFormat;
import pro1041.utils.MsgBox;
import pro1041.utils.Validator;

/**
 *
 * @author hanzvu
 */
public class ProductManagementFrame extends javax.swing.JInternalFrame {

    private ProductDAO productDAO;
    private ProductDetailDAO productDetailDAO;
    private BrandDAO brandDAO;
    private JFileChooser chooser;

    private List<Object[]> listProduct;
    private List<Object[]> listBrandDetail;
    private List<ProductDetail> listProductDetail;
    private Map<Integer, Brand> mapBrand;

    private DefaultTableModel productTableModel;
    private DefaultTableModel productDetailTableModel;
    private DefaultTableModel brandTableModel;

    private int index;
    private String imageName;
    private boolean isManager;

    private final String[] cboActivateModel = new String[]{null, "1", "0"};
    private final String[] cboQuantityStatusModel = new String[]{null, ">", "="};
    private final String[] cboOrderByModel = new String[]{"P.ID", "P.NAME", "SUM(PD.QUANTITY)", "MIN(PD.PRICE)", "MAX(PD.PRICE)", "MAX(PD.IMPORT_DATE)"};
    private final String[] cboOrderDirectionModel = new String[]{"DESC", "ASC"};

    /**
     * Creates new form ProductManagementFrame
     */
    public ProductManagementFrame() {
        initComponents();
        init();
    }

    private void init() {
        chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg");
        chooser.setFileFilter(filter);

        productDAO = new ProductDAO();
        brandDAO = new BrandDAO();
        productDetailDAO = new ProductDetailDAO();
        productTableModel = (DefaultTableModel) tblProduct.getModel();
        productDetailTableModel = (DefaultTableModel) tblProductDetail.getModel();

        fillToComboBox();

        resetAndFillToProductTable();

        tblProduct.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            updateIndexStatus();
        });

        tblBrand.getTableHeader().setFont(new java.awt.Font("Tahoma", 1, 16));
        tblProduct.getTableHeader().setFont(new java.awt.Font("Tahoma", 1, 16));
        tblProductDetail.getTableHeader().setFont(new java.awt.Font("Tahoma", 1, 16));

        isManager = Auth.isManager();
        if (isManager) {
            brandTableModel = (DefaultTableModel) tblBrand.getModel();
            resetAndFillToBrandTable();
        } else {
            tabPane.remove(palBrandManagement);
            btnSave.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnSaveProductDetail.setEnabled(false);
            btnResetProductDetail.setEnabled(false);
        }
    }

    private void fillToComboBox() {
        int indexBrand = cboBrand.getSelectedIndex();
        int indexProductBrand = cboProductBrand.getSelectedIndex();

        mapBrand = brandDAO.selectAll().stream().collect(Collectors.toMap(Brand::getId, Function.identity()));

        cboProductBrand.setModel(new DefaultComboBoxModel<>(mapBrand.values().stream().toArray(Brand[]::new)));

        DefaultComboBoxModel<Brand> cboModel = new DefaultComboBoxModel<>();
        cboModel.addElement(new Brand(-1, "Tất cả"));
        for (Brand brand : mapBrand.values()) {
            cboModel.addElement(brand);
        }
        cboBrand.setModel(cboModel);

        if (indexBrand != -1) {
            cboBrand.setSelectedIndex(indexBrand);
        }

        if (indexProductBrand != -1) {
            cboProductBrand.setSelectedIndex(indexProductBrand);
        }
    }

    private void clearForm() {
        txtID.setText("");
        txtName.setText("");
        txtDescription.setText("");
        productDetailTableModel.setRowCount(0);
        listProductDetail = new ArrayList<>();
        txtSize.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
        imageName = null;
        lblImage.setIcon(null);
    }

    private void updateState() {
        boolean edit = index > -1;
        boolean first = index == 0;
        boolean last = index == tblProduct.getRowCount() - 1;

        txtID.setEditable(!edit);
        btnSave.setEnabled(!edit && isManager);
        btnUpdate.setEnabled(edit & isManager);

        btnFirst.setEnabled(edit && !first);
        btnPre.setEnabled(edit && !first);
        btnNext.setEnabled(edit && !last);
        btnLast.setEnabled(edit && !last);
    }

    private void updateIndexStatus() {
        lblIndex.setText(tblProduct.getSelectedRow() + 1 + " / " + listProduct.size());
    }

    // Tab 1 : Danh sách sản phẩm
    private void resetAndFillToProductTable() {
        String sql = "SELECT P.ID, P.NAME, P.BRAND_ID, SUM(PD.QUANTITY), MIN(PD.PRICE), MAX(PD.PRICE), MAX(PD.IMPORT_DATE), P.ACTIVATE, P.DESCRIPTION FROM PRODUCT P INNER JOIN PRODUCT_DETAIL PD ON P.ID = PD.PRODUCT_ID GROUP BY P.ID, P.NAME, P.BRAND_ID , P.ACTIVATE, P.DESCRIPTION ORDER BY P.ID DESC";
        listProduct = JdbcHelper.getListOfArray(sql, 9);
        fillToProductTable(listProduct);
        clearForm();
        updateState();
    }

    private Object[] fetchProduct(String productID) {
        String sql = "SELECT P.ID, P.NAME, P.BRAND_ID, SUM(PD.QUANTITY), MIN(PD.PRICE), MAX(PD.PRICE), MAX(PD.IMPORT_DATE), P.ACTIVATE, P.DESCRIPTION FROM PRODUCT P INNER JOIN PRODUCT_DETAIL PD ON P.ID = PD.PRODUCT_ID WHERE P.ID = ? GROUP BY P.ID, P.NAME, P.BRAND_ID , P.ACTIVATE, P.DESCRIPTION";
        return JdbcHelper.getListOfArray(sql, 9, productID).get(0);
    }

    private void fillToProductTable(Object[] product) {
        productTableModel.addRow(new Object[]{product[0], product[1], mapBrand.get(product[2]).getName(), product[3], MoneyFormat.formatVND(product[4]), MoneyFormat.formatVND(product[5]), DateHelper.toString((Date) product[6]), (boolean) product[7] ? "Đang Bán" : "Ngừng Bán"});
    }

    private void fillToProductTable(int index, Object[] product) {
        productTableModel.setValueAt(product[0], index, 0);
        productTableModel.setValueAt(product[1], index, 1);
        productTableModel.setValueAt(mapBrand.get(product[2]).getName(), index, 2);
        productTableModel.setValueAt(product[3], index, 3);
        productTableModel.setValueAt(MoneyFormat.formatVND(product[4]), index, 4);
        productTableModel.setValueAt(MoneyFormat.formatVND(product[5]), index, 5);
        productTableModel.setValueAt(DateHelper.toString((Date) product[6]), index, 6);
        productTableModel.setValueAt((boolean) product[7] ? "Đang Bán" : "Ngừng Bán", index, 7);
    }

    private void fillToProductTable(List<Object[]> list) {
        productTableModel.setRowCount(0);
        for (Object[] product : list) {
            ProductManagementFrame.this.fillToProductTable(product);
        }
        index = -1;
        updateIndexStatus();
    }

    // Tab 2 : Cập nhật thông tin sản phẩm
    private Product getProductByForm() {
        if (Validator.isNull(txtID, "Không được để trống mã sản phẩm") || Validator.isNull(txtName, "Không được để trống tên sản phẩm")) {
            return null;
        }
        return new Product(txtID.getText(), txtName.getText(), txtDescription.getText(), rdoActive.isSelected(), ((Brand) cboProductBrand.getSelectedItem()).getId());
    }

    private ProductDetail getProductDetailByForm() {
        if (Validator.isNull(txtSize, "Không được để trống cỡ sản phẩm")
                || Validator.isNull(txtPrice, "Không được để trống giá sản phẩm")
                || Validator.isNull(txtQuantity, "Không được để trống số lượng sản phẩm")) {
            return null;
        }

        ProductDetail productDetail = new ProductDetail();

        try {
            productDetail.setSize(Float.valueOf(txtSize.getText()));
        } catch (NumberFormatException e) {
            txtSize.requestFocus();
            MsgBox.alert(this, "Cỡ sản phẩm phải là số");
            return null;
        }

        try {
            productDetail.setPrice(Double.parseDouble(txtPrice.getText()));
        } catch (NumberFormatException e) {
            txtPrice.requestFocus();
            MsgBox.alert(this, "Giá sản phẩm phải là số");
            return null;
        }

        try {
            productDetail.setQuantity(Integer.valueOf(txtQuantity.getText()));
        } catch (NumberFormatException e) {
            txtQuantity.requestFocus();
            MsgBox.alert(this, "Số lượng sản phẩm phải là số");
            return null;
        }

        if (productDetail.getSize() <= 0) {
            txtSize.requestFocus();
            MsgBox.alert(this, "Cỡ sản phẩm phải là số dương");
            return null;
        }

        if (productDetail.getPrice() < 0) {
            txtPrice.requestFocus();
            MsgBox.alert(this, "Giá sản phẩm phải là số dương");
            return null;
        }

        if (productDetail.getQuantity() < 0) {
            txtQuantity.requestFocus();
            MsgBox.alert(this, "Số lượng sản phẩm phải là số dương");
            return null;
        }

        int index = tblProductDetail.getSelectedRow();

        if (index == -1 || productDetail.getQuantity() > listProductDetail.get(index).getQuantity()) {
            productDetail.setImportDate(new Date(DateHelper.now().getTime()));
        } else {
            productDetail.setImportDate(listProductDetail.get(index).getImportDate());
        }

        productDetail.setImage(imageName);

        if (rdoUnisex.isSelected()) {
            productDetail.setType(Type.UNISEX);
        } else if (rdoMale.isSelected()) {
            productDetail.setType(Type.MALE);
        } else if (rdoFemale.isSelected()) {
            productDetail.setType(Type.FEMALE);
        } else if (rdoKid.isSelected()) {
            productDetail.setType(Type.KID);
        } else {
            MsgBox.alert(this, "Chưa chọn loại sản phẩm");
            return null;
        }

        if (!txtBarcode.getText().isEmpty()) {
            productDetail.setBarcode(txtBarcode.getText());
        }

        return productDetail;
    }

    private void showProductInfo(Object[] product) {
        txtID.setText(product[0].toString());
        txtName.setText(product[1].toString());
        txtDescription.setText(product[8].toString());
        if ((boolean) product[7]) {
            rdoActive.setSelected(true);
        } else {
            rdoDeactive.setSelected(true);
        }
        for (int i = 0; i < cboProductBrand.getItemCount(); i++) {
            if (cboProductBrand.getItemAt(i).getId() == (int) product[2]) {
                cboProductBrand.setSelectedIndex(i);
                break;
            }
        }

        listProductDetail = productDetailDAO.selectAllByProductID(product[0].toString());
        fillToProductDetailTable(listProductDetail);
        if (listProductDetail.size() > 0) {
            tblProductDetail.setRowSelectionInterval(0, 0);
            showProductDetailInfo(listProductDetail.get(0));
        }
        tabPane.setSelectedIndex(1);
    }

    private void showProductDetailInfo(ProductDetail productDetail) {
        txtSize.setText(String.format("%.1f", productDetail.getSize()));
        txtPrice.setText(String.format("%.0f", productDetail.getPrice()));
        txtQuantity.setText(String.valueOf(productDetail.getQuantity()));
        txtBarcode.setText(productDetail.getBarcode());
        if (productDetail.getImage() != null) {
            setLblImageIcon(productDetail.getImage());
        } else {
            imageName = null;
            lblImage.setIcon(null);
        }
        switch (productDetail.getType()) {
            case UNISEX:
                rdoUnisex.setSelected(true);
                break;
            case MALE:
                rdoMale.setSelected(true);
                break;
            case FEMALE:
                rdoFemale.setSelected(true);
                break;
            case KID:
                rdoKid.setSelected(true);
                break;
        }
    }

    private void fillToProductDetailTable(ProductDetail productDetail) {
        productDetailTableModel.addRow(new Object[]{productDetail.getType().toString(), productDetail.getSize(), MoneyFormat.formatVND(productDetail.getPrice()), DateHelper.toString(productDetail.getImportDate()), productDetail.getQuantity(), productDetail.getImage(), productDetail.getBarcode()});
    }

    private void fillToProductDetailTable(int index, ProductDetail productDetail) {
        productDetailTableModel.setValueAt(productDetail.getType().toString(), index, 0);
        productDetailTableModel.setValueAt(productDetail.getSize(), index, 1);
        productDetailTableModel.setValueAt(MoneyFormat.formatVND(productDetail.getPrice()), index, 2);
        productDetailTableModel.setValueAt(DateHelper.toString(productDetail.getImportDate()), index, 3);
        productDetailTableModel.setValueAt(productDetail.getQuantity(), index, 4);
        productDetailTableModel.setValueAt(productDetail.getImage(), index, 5);
        productDetailTableModel.setValueAt(productDetail.getBarcode(), index, 6);
    }

    private void fillToProductDetailTable(List<ProductDetail> list) {
        productDetailTableModel.setRowCount(0);
        for (ProductDetail productDetail : list) {
            fillToProductDetailTable(productDetail);
        }
    }

    private void setLblImageIcon(String img) {
        imageName = img;
        lblImage.setIcon(ImageHelper.read(imageName));
    }

    // Tab 3 : Quản lý hãng
    private void showBrandDetail(Object[] object) {
        txtBrandName.setText(object[1].toString());
    }

    private void resetAndFillToBrandTable() {
        String sql = "SELECT BRAND.ID, BRAND.NAME, COALESCE(SUM(PRODUCT_DETAIL.QUANTITY),0) FROM BRAND LEFT JOIN PRODUCT ON BRAND.ID = PRODUCT.BRAND_ID LEFT JOIN PRODUCT_DETAIL ON PRODUCT.ID = PRODUCT_DETAIL.PRODUCT_ID GROUP BY BRAND.ID, BRAND.NAME";
        listBrandDetail = JdbcHelper.getListOfArray(sql, 3);
        fillToBrandTable(listBrandDetail);
        btnUpdateBrand.setEnabled(false);
    }

    private void fillToBrandTable(Object[] brand) {
        brandTableModel.addRow(brand);
    }

    private void fillToBrandTable(int index, Object[] brand) {
        brandTableModel.setValueAt(brand[0], index, 0);
        brandTableModel.setValueAt(brand[1], index, 1);
        brandTableModel.setValueAt(brand[2], index, 2);
    }

    private void fillToBrandTable(List<Object[]> list) {
        brandTableModel.setRowCount(0);
        for (Object[] brand : list) {
            fillToBrandTable(brand);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        typeButtonGroup = new javax.swing.ButtonGroup();
        activeButtonGroup = new javax.swing.ButtonGroup();
        tabPane = new javax.swing.JTabbedPane();
        palListProduct = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblProduct = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnShowAll = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        cboBrand = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        cboQuantityStatus = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        cboOrderBy = new javax.swing.JComboBox<>();
        cboOrderDirection = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        lblIndex = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        cboActivate = new javax.swing.JComboBox<>();
        btnSearchByBarcode = new javax.swing.JButton();
        palUpdateProduct = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtID = new javax.swing.JTextField();
        cboProductBrand = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        lblImage = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProductDetail = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        rdoKid = new javax.swing.JRadioButton();
        rdoUnisex = new javax.swing.JRadioButton();
        rdoFemale = new javax.swing.JRadioButton();
        rdoMale = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        txtSize = new javax.swing.JTextField();
        txtPrice = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btnSaveProductDetail = new javax.swing.JButton();
        txtQuantity = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btnPlus = new javax.swing.JButton();
        btnMinus = new javax.swing.JButton();
        btnResetProductDetail = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        txtBarcode = new javax.swing.JTextField();
        btnScan = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnPre = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        rdoActive = new javax.swing.JRadioButton();
        rdoDeactive = new javax.swing.JRadioButton();
        palBrandManagement = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblBrand = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        txtBrandName = new javax.swing.JTextField();
        btnSaveBrand = new javax.swing.JButton();
        btnUpdateBrand = new javax.swing.JButton();
        btnResetBrand = new javax.swing.JButton();
        btnSearchBrand = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setClosable(true);
        setIconifiable(true);
        setTitle("ShoeStoreSys - Quản Lý Sản Phẩm");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logoExtraSmall.jpg"))); // NOI18N
        setPreferredSize(new java.awt.Dimension(1200, 800));
        setVisible(false);

        tabPane.setFocusable(false);
        tabPane.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tabPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabPaneStateChanged(evt);
            }
        });

        tblProduct.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Tên", "Hãng", "Số Lượng", "Giá Thấp Nhất", "Giá Cao Nhất", "Ngày Nhập Mới", "Tình Trạng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
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
        jScrollPane3.setViewportView(tblProduct);

        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtSearch.setMargin(new java.awt.Insets(2, 15, 2, 2));

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

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel6.setText("Hãng");

        cboBrand.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboBrand.setFocusable(false);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel10.setText("Tình Trạng");

        cboQuantityStatus.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboQuantityStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất Cả", "Còn Hàng", "Hết Hàng" }));
        cboQuantityStatus.setFocusable(false);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel11.setText("Sắp Xếp Theo");

        cboOrderBy.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboOrderBy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ID", "Tên", "Số Lượng", "Giá Thấp Nhất", "Giá Cao Nhất", "Ngày Nhập Mới" }));
        cboOrderBy.setFocusable(false);

        cboOrderDirection.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboOrderDirection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Giảm Dần", "Tăng Dần" }));
        cboOrderDirection.setFocusable(false);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel12.setText("Từ Khóa");

        lblIndex.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblIndex.setForeground(new java.awt.Color(255, 102, 102));
        lblIndex.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIndex.setText("Index");
        lblIndex.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel15.setText("Số Lượng");

        cboActivate.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboActivate.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất Cả", "Đang Bán", "Ngừng Bán" }));
        cboActivate.setFocusable(false);

        btnSearchByBarcode.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnSearchByBarcode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/scanner.png"))); // NOI18N
        btnSearchByBarcode.setText("Quét Mã");
        btnSearchByBarcode.setFocusable(false);
        btnSearchByBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchByBarcodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout palListProductLayout = new javax.swing.GroupLayout(palListProduct);
        palListProduct.setLayout(palListProductLayout);
        palListProductLayout.setHorizontalGroup(
            palListProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(palListProductLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(palListProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(palListProductLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(palListProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(palListProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboBrand, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboActivate, 0, 162, Short.MAX_VALUE))
                        .addGap(27, 27, 27)
                        .addGroup(palListProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(palListProductLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboQuantityStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboOrderBy, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(palListProductLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSearch)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(palListProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboOrderDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(palListProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnShowAll, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                            .addComponent(btnSearchByBarcode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palListProductLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        palListProductLayout.setVerticalGroup(
            palListProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palListProductLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(palListProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel12)
                    .addComponent(btnSearchByBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(palListProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cboQuantityStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(cboOrderBy, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboOrderDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(cboActivate, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnShowAll, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        palListProductLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnSearch, cboBrand, txtSearch});

        tabPane.addTab("Danh Sách", palListProduct);

        palUpdateProduct.setPreferredSize(new java.awt.Dimension(1200, 800));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setText("ID");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel2.setText("Tên Sản Phẩm");

        txtName.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        txtID.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        cboProductBrand.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboProductBrand.setFocusable(false);

        txtDescription.setColumns(20);
        txtDescription.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtDescription.setRows(5);
        txtDescription.setMargin(new java.awt.Insets(10, 10, 10, 10));
        jScrollPane1.setViewportView(txtDescription);

        lblImage.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblImage.setPreferredSize(new java.awt.Dimension(300, 300));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImageMouseReleased(evt);
            }
        });

        tblProductDetail.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblProductDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Loại", "Size", "Giá", "Ngày Nhập", "Số Lượng", "Hình", "Barcode"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProductDetail.setRowHeight(30);
        tblProductDetail.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblProductDetail.getTableHeader().setReorderingAllowed(false);
        tblProductDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblProductDetailMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblProductDetail);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Loại", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 16))); // NOI18N

        typeButtonGroup.add(rdoKid);
        rdoKid.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        rdoKid.setText("Kid");
        rdoKid.setFocusable(false);

        typeButtonGroup.add(rdoUnisex);
        rdoUnisex.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        rdoUnisex.setSelected(true);
        rdoUnisex.setText("Unisex");
        rdoUnisex.setFocusable(false);

        typeButtonGroup.add(rdoFemale);
        rdoFemale.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        rdoFemale.setText("Female");
        rdoFemale.setFocusable(false);

        typeButtonGroup.add(rdoMale);
        rdoMale.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        rdoMale.setText("Male");
        rdoMale.setFocusable(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdoFemale)
                    .addComponent(rdoUnisex))
                .addGap(39, 39, 39)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdoKid)
                    .addComponent(rdoMale))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoUnisex)
                    .addComponent(rdoKid))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoFemale)
                    .addComponent(rdoMale))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel7.setText("Size");

        txtSize.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtSize.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtPrice.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtPrice.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel8.setText("Giá");

        btnSaveProductDetail.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnSaveProductDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/download-25.png"))); // NOI18N
        btnSaveProductDetail.setText("Lưu Loại Giày");
        btnSaveProductDetail.setFocusable(false);
        btnSaveProductDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveProductDetailActionPerformed(evt);
            }
        });

        txtQuantity.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtQuantity.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel9.setText("Số Lượng");

        btnPlus.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnPlus.setText("+");
        btnPlus.setFocusable(false);
        btnPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlusActionPerformed(evt);
            }
        });

        btnMinus.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnMinus.setText("-");
        btnMinus.setFocusable(false);
        btnMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMinusActionPerformed(evt);
            }
        });

        btnResetProductDetail.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnResetProductDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/refresh-25.png"))); // NOI18N
        btnResetProductDetail.setText("Loại Giày Mới");
        btnResetProductDetail.setFocusable(false);
        btnResetProductDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetProductDetailActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel16.setText("Barcode");

        txtBarcode.setEditable(false);
        txtBarcode.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtBarcode.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        btnScan.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnScan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/scanner.png"))); // NOI18N
        btnScan.setText("Quét");
        btnScan.setFocusable(false);
        btnScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtSize, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addComponent(txtPrice))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtQuantity, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtBarcode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnScan, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSaveProductDetail)
                    .addComponent(btnResetProductDetail))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnResetProductDetail, btnSaveProductDetail});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSaveProductDetail)
                            .addComponent(btnPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnResetProductDetail))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7)
                                .addComponent(txtSize, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9)
                                .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel8)
                                .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel16)
                                .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnScan, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnResetProductDetail, btnSaveProductDetail, txtPrice, txtSize});

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel4.setText("Hãng");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel5.setText("Mô Tả");

        btnSave.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/add-to-collection-30.png"))); // NOI18N
        btnSave.setText("Thêm Sản Phẩm");
        btnSave.setFocusable(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/update-40.png"))); // NOI18N
        btnUpdate.setText("Cập Nhật");
        btnUpdate.setFocusable(false);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnReset.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/refresh-30.png"))); // NOI18N
        btnReset.setText("Làm Mới");
        btnReset.setFocusable(false);
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnFirst.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/first-1-30.png"))); // NOI18N
        btnFirst.setFocusable(false);
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });

        btnPre.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnPre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/back-30.png"))); // NOI18N
        btnPre.setFocusable(false);
        btnPre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreActionPerformed(evt);
            }
        });

        btnNext.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/next-30.png"))); // NOI18N
        btnNext.setFocusable(false);
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnLast.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/last-1-30.png"))); // NOI18N
        btnLast.setFocusable(false);
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel14.setText("Trạng Thái");

        activeButtonGroup.add(rdoActive);
        rdoActive.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        rdoActive.setSelected(true);
        rdoActive.setText("Đang Bán");
        rdoActive.setFocusable(false);

        activeButtonGroup.add(rdoDeactive);
        rdoDeactive.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        rdoDeactive.setText("Ngừng Bán");
        rdoDeactive.setFocusable(false);

        javax.swing.GroupLayout palUpdateProductLayout = new javax.swing.GroupLayout(palUpdateProduct);
        palUpdateProduct.setLayout(palUpdateProductLayout);
        palUpdateProductLayout.setHorizontalGroup(
            palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(palUpdateProductLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(palUpdateProductLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addGroup(palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cboProductBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtName)
                                .addComponent(txtID)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(palUpdateProductLayout.createSequentialGroup()
                                .addComponent(rdoActive)
                                .addGap(18, 18, 18)
                                .addComponent(rdoDeactive)))
                        .addGap(86, 86, 86)
                        .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(palUpdateProductLayout.createSequentialGroup()
                        .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnPre, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        palUpdateProductLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnReset, btnSave, btnUpdate});

        palUpdateProductLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnFirst, btnLast, btnNext, btnPre});

        palUpdateProductLayout.setVerticalGroup(
            palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(palUpdateProductLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(palUpdateProductLayout.createSequentialGroup()
                        .addGroup(palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboProductBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(rdoActive)
                            .addComponent(rdoDeactive))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(palUpdateProductLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel5)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(palUpdateProductLayout.createSequentialGroup()
                        .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(palUpdateProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPre, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        palUpdateProductLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboProductBrand, txtID, txtName});

        palUpdateProductLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnReset, btnSave, btnUpdate});

        palUpdateProductLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFirst, btnLast, btnNext, btnPre});

        tabPane.addTab("Cập Nhật", palUpdateProduct);

        tblBrand.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblBrand.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Hãng", "Tên Hãng", "Tổng Số Sản Phẩm"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBrand.setRowHeight(30);
        tblBrand.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblBrand.getTableHeader().setReorderingAllowed(false);
        tblBrand.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblBrandMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(tblBrand);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel3.setText("Tên Hãng");

        txtBrandName.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtBrandName.setMargin(new java.awt.Insets(2, 15, 2, 2));

        btnSaveBrand.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnSaveBrand.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/add-tag-30.png"))); // NOI18N
        btnSaveBrand.setText("Thêm Hãng");
        btnSaveBrand.setFocusable(false);
        btnSaveBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveBrandActionPerformed(evt);
            }
        });

        btnUpdateBrand.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnUpdateBrand.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/update-tag-30.png"))); // NOI18N
        btnUpdateBrand.setText("Cập Nhật");
        btnUpdateBrand.setFocusable(false);
        btnUpdateBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateBrandActionPerformed(evt);
            }
        });

        btnResetBrand.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnResetBrand.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/refresh-30.png"))); // NOI18N
        btnResetBrand.setText("Làm Mới");
        btnResetBrand.setFocusable(false);
        btnResetBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetBrandActionPerformed(evt);
            }
        });

        btnSearchBrand.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnSearchBrand.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/search-30.png"))); // NOI18N
        btnSearchBrand.setText("Tìm Kiếm");
        btnSearchBrand.setFocusable(false);
        btnSearchBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchBrandActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 40)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 153, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("QUẢN LÝ HÃNG SẢN PHẨM");

        javax.swing.GroupLayout palBrandManagementLayout = new javax.swing.GroupLayout(palBrandManagement);
        palBrandManagement.setLayout(palBrandManagementLayout);
        palBrandManagementLayout.setHorizontalGroup(
            palBrandManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(palBrandManagementLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palBrandManagementLayout.createSequentialGroup()
                .addGap(89, 89, 89)
                .addComponent(jLabel3)
                .addGap(35, 35, 35)
                .addComponent(txtBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                .addGroup(palBrandManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSearchBrand, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSaveBrand, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(palBrandManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUpdateBrand)
                    .addComponent(btnResetBrand))
                .addGap(79, 79, 79))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palBrandManagementLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        palBrandManagementLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnResetBrand, btnSaveBrand, btnSearchBrand, btnUpdateBrand});

        palBrandManagementLayout.setVerticalGroup(
            palBrandManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palBrandManagementLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(palBrandManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palBrandManagementLayout.createSequentialGroup()
                        .addGroup(palBrandManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnResetBrand)
                            .addComponent(btnSearchBrand))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(palBrandManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnUpdateBrand)
                            .addComponent(btnSaveBrand))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palBrandManagementLayout.createSequentialGroup()
                        .addGroup(palBrandManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(55, 55, 55)))
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        palBrandManagementLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnResetBrand, btnSaveBrand, btnSearchBrand, btnUpdateBrand, txtBrandName});

        tabPane.addTab("Quản Lý Hãng", palBrandManagement);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1184, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 764, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveProductDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveProductDetailActionPerformed
        ProductDetail productDetail = getProductDetailByForm();
        if (productDetail == null) {
            return;
        }

        int index = tblProductDetail.getSelectedRow();

        for (int i = 0; i < listProductDetail.size(); i++) {
            if (i == index) {
                continue;
            }
            ProductDetail x = listProductDetail.get(i);
            if (x.getType() == productDetail.getType() && x.getSize() == productDetail.getSize()) {
                MsgBox.alert(null, "Loại sản phẩm đã tồn tại !");
                return;
            }
            if (x.getBarcode() != null && productDetail.getBarcode() != null && x.getBarcode().equals(productDetail.getBarcode())) {
                MsgBox.alert(null, "Mã vạch sản phẩm đã tồn tại !");
                return;
            }
        }

        if (index > -1) {
            productDetail.setId(listProductDetail.get(index).getId());
            listProductDetail.set(index, productDetail);
            fillToProductDetailTable(index, productDetail);
            return;
        }

        listProductDetail.add(productDetail);
        fillToProductDetailTable(productDetail);
    }//GEN-LAST:event_btnSaveProductDetailActionPerformed

    private void btnShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowAllActionPerformed
        resetAndFillToProductTable();
        txtSearch.setText("");
        cboBrand.setSelectedIndex(0);
        cboQuantityStatus.setSelectedIndex(0);
        cboActivate.setSelectedIndex(0);
        cboOrderBy.setSelectedIndex(0);
        cboOrderDirection.setSelectedIndex(0);
    }//GEN-LAST:event_btnShowAllActionPerformed

    private void tblProductMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductMouseReleased
        if (SwingUtilities.isLeftMouseButton(evt)) {
            if (evt.getClickCount() == 2) {
                index = tblProduct.getSelectedRow();
                showProductInfo(listProduct.get(index));
            }
            updateState();
        }
    }//GEN-LAST:event_tblProductMouseReleased

    private void btnResetProductDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetProductDetailActionPerformed
        tblProductDetail.clearSelection();
        txtSize.setText("0");
        txtPrice.setText("0");
        txtQuantity.setText("1");
        txtBarcode.setText("");
        imageName = null;
        lblImage.setIcon(null);
    }//GEN-LAST:event_btnResetProductDetailActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (listProductDetail.isEmpty()) {
            MsgBox.alert(this, "Chưa có loại sản phẩm nào !");
            return;
        }

        Product product = getProductByForm();
        if (product == null) {
            return;
        }

        if (JdbcHelper.getListOfArray("SELECT ID FROM PRODUCT WHERE ID = ?", 1, product.getId()).size() > 0) {
            txtID.requestFocus();
            MsgBox.alert(null, "ID sản phẩm đã tồn tại !");
            return;
        }

        if (JdbcHelper.getListOfArray("SELECT ID FROM PRODUCT WHERE NAME = ?", 1, product.getName()).size() > 0) {
            txtName.requestFocus();
            MsgBox.alert(null, "Tên sản phẩm đã tồn tại !");
            return;
        }

        productDAO.insert(product);
        for (ProductDetail productDetail : listProductDetail) {
            if (productDetail.getQuantity() > 0) {
                productDetail.setProductID(product.getId());
                productDetailDAO.insert(productDetail);
            }
        }

        Object[] fetchedProduct = fetchProduct(product.getId());
        listProduct.add(fetchedProduct);
        fillToProductTable(fetchedProduct);
        index = listProduct.size() - 1;
        tblProduct.setRowSelectionInterval(index, index);
        updateState();
        MsgBox.alert(this, "Thêm sản phẩm thành công !");

    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        clearForm();
        tblProduct.clearSelection();
        index = -1;
        updateState();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (listProductDetail.isEmpty()) {
            MsgBox.alert(this, "Chưa có loại sản phẩm nào !");
            return;
        }

        Product product = getProductByForm();
        if (product == null) {
            return;
        }

        if (JdbcHelper.getListOfArray("SELECT ID FROM PRODUCT WHERE ID = ?", 1, product.getId()).size() > 1) {
            txtID.requestFocus();
            MsgBox.alert(null, "ID sản phẩm đã tồn tại !");
            return;
        }

        if (JdbcHelper.getListOfArray("SELECT ID FROM PRODUCT WHERE NAME = ? AND ID != ?", 1, product.getName(), product.getId()).size() > 0) {
            txtName.requestFocus();
            MsgBox.alert(null, "Tên sản phẩm đã tồn tại !");
            return;
        }

        productDAO.update(product);
        for (ProductDetail productDetail : listProductDetail) {
            productDetail.setProductID(product.getId());
            if (productDetail.getId() > 0) {
                productDetailDAO.update(productDetail);
            } else if (productDetail.getQuantity() > 0) {
                productDetailDAO.insert(productDetail);
            }
        }
        Object[] fetchedProduct = fetchProduct(product.getId());
        listProduct.set(index, fetchedProduct);
        fillToProductTable(index, fetchedProduct);
        tblProduct.setRowSelectionInterval(index, index);
        MsgBox.alert(this, "Cập nhật sản phẩm thành công");
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        index = 0;
        tblProduct.setRowSelectionInterval(0, 0);
        showProductInfo(listProduct.get(0));
        updateState();
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreActionPerformed
        index--;
        tblProduct.setRowSelectionInterval(index, index);
        showProductInfo(listProduct.get(index));
        updateState();
    }//GEN-LAST:event_btnPreActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        index++;
        tblProduct.setRowSelectionInterval(index, index);
        showProductInfo(listProduct.get(index));
        updateState();
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        index = tblProduct.getRowCount() - 1;
        tblProduct.setRowSelectionInterval(index, index);
        showProductInfo(listProduct.get(index));
        updateState();
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMinusActionPerformed
        try {
            int quantity = Integer.parseInt(txtQuantity.getText());
            if (quantity > 1) {
                txtQuantity.setText(String.valueOf(quantity - 1));
            }
        } catch (NumberFormatException e) {
            txtQuantity.setText("1");
        }
    }//GEN-LAST:event_btnMinusActionPerformed

    private void btnPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlusActionPerformed
        try {
            int quantity = Integer.parseInt(txtQuantity.getText());
            txtQuantity.setText(String.valueOf(quantity + 1));
        } catch (NumberFormatException e) {
            txtQuantity.setText("1");
        }
    }//GEN-LAST:event_btnPlusActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String search = "%" + txtSearch.getText() + "%";

        String prefix = "SELECT P.ID, P.NAME, P.BRAND_ID, SUM(PD.QUANTITY), MIN(PD.PRICE), MAX(PD.PRICE), MAX(PD.IMPORT_DATE), P.ACTIVATE, P.DESCRIPTION FROM PRODUCT P INNER JOIN PRODUCT_DETAIL PD ON P.ID = PD.PRODUCT_ID WHERE ( P.ID LIKE ? OR P.NAME LIKE ? )";
        String suffix = " GROUP BY P.ID, P.NAME, P.BRAND_ID , P.ACTIVATE, P.DESCRIPTION";
        String orderBy = " ORDER BY " + cboOrderByModel[cboOrderBy.getSelectedIndex()] + " " + cboOrderDirectionModel[cboOrderDirection.getSelectedIndex()];

        if (cboQuantityStatus.getSelectedIndex() != 0) {
            suffix += " HAVING SUM(PD.QUANTITY) " + cboQuantityStatusModel[cboQuantityStatus.getSelectedIndex()] + " 0";
        }

        if (cboActivate.getSelectedIndex() != 0) {
            prefix += " AND P.ACTIVATE = " + cboActivateModel[cboActivate.getSelectedIndex()];
        }

        if (cboBrand.getSelectedIndex() != 0) {
            prefix += " AND P.BRAND_ID = " + ((Brand) cboBrand.getSelectedItem()).getId();
        }

        listProduct = JdbcHelper.getListOfArray(prefix + suffix + orderBy, 9, search, search);
        fillToProductTable(listProduct);
        updateState();
        clearForm();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (isManager && chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = ImageHelper.resizeAndSave(chooser.getSelectedFile(), 300, 300);
            setLblImageIcon(file.getName());
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void tblProductDetailMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductDetailMousePressed
        Point point = evt.getPoint();
        int index = tblProductDetail.rowAtPoint(point);
        if (SwingUtilities.isRightMouseButton(evt)) {
            tblProductDetail.setRowSelectionInterval(index, index);
        }
        showProductDetailInfo(listProductDetail.get(index));
    }//GEN-LAST:event_tblProductDetailMousePressed

    private void btnResetBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetBrandActionPerformed
        resetAndFillToBrandTable();
        txtBrandName.setText("");
        btnSaveBrand.setEnabled(true);
        btnSearchBrand.setEnabled(true);
    }//GEN-LAST:event_btnResetBrandActionPerformed

    private void btnSearchBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchBrandActionPerformed
        String sql = "SELECT BRAND.ID, BRAND.NAME, COALESCE(SUM(PRODUCT_DETAIL.QUANTITY),0) FROM BRAND LEFT JOIN PRODUCT ON BRAND.ID = PRODUCT.BRAND_ID LEFT JOIN PRODUCT_DETAIL ON PRODUCT.ID = PRODUCT_DETAIL.PRODUCT_ID WHERE BRAND.NAME LIKE ? GROUP BY BRAND.ID, BRAND.NAME";
        listBrandDetail = JdbcHelper.getListOfArray(sql, 3, txtBrandName.getText() + "%");
        fillToBrandTable(listBrandDetail);
    }//GEN-LAST:event_btnSearchBrandActionPerformed

    private void btnUpdateBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateBrandActionPerformed
        if (Validator.isNull(txtBrandName, "Vui lòng nhập tên hãng sản phẩm !")) {
            return;
        }
        int index = tblBrand.getSelectedRow();
        Brand brand = new Brand((int) listBrandDetail.get(index)[0], txtBrandName.getText());

        if (JdbcHelper.getListOfArray("SELECT ID FROM BRAND WHERE NAME = ? AND ID != ?", 1, brand.getName(), brand.getId()).size() > 0) {
            txtBrandName.requestFocus();
            MsgBox.alert(null, "Tên hãng sản phẩm đã tồn tại !");
            return;
        }

        brandDAO.update(brand);
        listBrandDetail.get(index)[1] = brand.getName();
        fillToBrandTable(index, listBrandDetail.get(index));
        fillToComboBox();
        MsgBox.alert(this, "Cập nhật hãng sản phẩm thành công !");
    }//GEN-LAST:event_btnUpdateBrandActionPerformed

    private void btnSaveBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveBrandActionPerformed
        if (Validator.isNull(txtBrandName, "Vui lòng nhập tên hãng sản phẩm !")) {
            return;
        }
        Brand brand = new Brand(txtBrandName.getText());

        if (JdbcHelper.getListOfArray("SELECT ID FROM BRAND WHERE NAME = ?", 1, brand.getName()).size() > 0) {
            txtBrandName.requestFocus();
            MsgBox.alert(null, "Tên hãng sản phẩm đã tồn tại !");
            return;
        }

        brandDAO.insert(brand);
        int index = listBrandDetail.size();
        Object[] brandObj = new Object[]{brand.getId(), brand.getName(), 0};
        listBrandDetail.add(brandObj);
        fillToBrandTable(brandObj);
        tblBrand.setRowSelectionInterval(index, index);

        btnSaveBrand.setEnabled(false);
        btnSearchBrand.setEnabled(false);
        btnUpdateBrand.setEnabled(true);
        fillToComboBox();
        MsgBox.alert(this, "Thêm hãng sản phẩm thành công !");
    }//GEN-LAST:event_btnSaveBrandActionPerformed

    private void tblBrandMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBrandMouseReleased
        if (SwingUtilities.isLeftMouseButton(evt)) {
            showBrandDetail(listBrandDetail.get(tblBrand.getSelectedRow()));
            btnSaveBrand.setEnabled(false);
            btnSearchBrand.setEnabled(false);
            btnUpdateBrand.setEnabled(true);
        }
    }//GEN-LAST:event_tblBrandMouseReleased

    private void tabPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabPaneStateChanged
        if (tabPane.getSelectedIndex() == 2) {
            resetAndFillToBrandTable();
        }
    }//GEN-LAST:event_tabPaneStateChanged

    private void btnScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScanActionPerformed
        ScanBarcodeDialog scanBarcodeDialog = new ScanBarcodeDialog(null, true);
        scanBarcodeDialog.setVisible(true);
        String result = scanBarcodeDialog.getResult();
        if (result == null) {
            return;
        }

        int index = tblProductDetail.getSelectedRow();
        if (index > -1) {
            int id = listProductDetail.get(index).getId();
            String sql = "SELECT ID FROM PRODUCT_DETAIL WHERE BARCODE = ? AND ID != ?";
            if (JdbcHelper.getListOfArray(sql, 1, result, id).size() > 0) {
                MsgBox.alert(null, "Mã vạch sản phẩm đã tồn tại !");
                return;
            }
        }
        txtBarcode.setText(result);
    }//GEN-LAST:event_btnScanActionPerformed

    private void btnSearchByBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchByBarcodeActionPerformed
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

        String sql = "SELECT P.ID, P.NAME, P.BRAND_ID, SUM(PD.QUANTITY), MIN(PD.PRICE), MAX(PD.PRICE), MAX(PD.IMPORT_DATE), P.ACTIVATE, P.DESCRIPTION FROM PRODUCT P INNER JOIN PRODUCT_DETAIL PD ON P.ID = PD.PRODUCT_ID WHERE P.ID = ? GROUP BY P.ID, P.NAME, P.BRAND_ID , P.ACTIVATE, P.DESCRIPTION";
        listProduct = JdbcHelper.getListOfArray(sql, 9, productDetail.getProductID());
        fillToProductTable(listProduct);
        index = 0;
        tblProduct.setRowSelectionInterval(0, 0);
        showProductInfo(listProduct.get(index));
        for (int i = 0; i < listProductDetail.size(); i++) {
            String bc = listProductDetail.get(i).getBarcode();
            if (bc != null && bc.equals(barcode)) {
                tblProductDetail.setRowSelectionInterval(i, i);
                showProductDetailInfo(listProductDetail.get(i));
                break;
            }
        }
        updateState();
    }//GEN-LAST:event_btnSearchByBarcodeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup activeButtonGroup;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnMinus;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPlus;
    private javax.swing.JButton btnPre;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnResetBrand;
    private javax.swing.JButton btnResetProductDetail;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveBrand;
    private javax.swing.JButton btnSaveProductDetail;
    private javax.swing.JButton btnScan;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchBrand;
    private javax.swing.JButton btnSearchByBarcode;
    private javax.swing.JButton btnShowAll;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpdateBrand;
    private javax.swing.JComboBox<String> cboActivate;
    private javax.swing.JComboBox<Brand> cboBrand;
    private javax.swing.JComboBox<String> cboOrderBy;
    private javax.swing.JComboBox<String> cboOrderDirection;
    private javax.swing.JComboBox<Brand> cboProductBrand;
    private javax.swing.JComboBox<String> cboQuantityStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblIndex;
    private javax.swing.JPanel palBrandManagement;
    private javax.swing.JPanel palListProduct;
    private javax.swing.JPanel palUpdateProduct;
    private javax.swing.JRadioButton rdoActive;
    private javax.swing.JRadioButton rdoDeactive;
    private javax.swing.JRadioButton rdoFemale;
    private javax.swing.JRadioButton rdoKid;
    private javax.swing.JRadioButton rdoMale;
    private javax.swing.JRadioButton rdoUnisex;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JTable tblBrand;
    private javax.swing.JTable tblProduct;
    private javax.swing.JTable tblProductDetail;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextField txtBrandName;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSize;
    private javax.swing.ButtonGroup typeButtonGroup;
    // End of variables declaration//GEN-END:variables
}
