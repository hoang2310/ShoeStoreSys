package pro1041.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import pro1041.dao.BrandDAO;
import pro1041.entity.Brand;
import pro1041.utils.JdbcHelper;
import pro1041.utils.MoneyFormat;
import org.jfree.data.category.DefaultCategoryDataset;
import pro1041.utils.MsgBox;

public class StatisticFrame extends javax.swing.JInternalFrame {

    private BrandDAO brandDAO;

    private DefaultTableModel tkdtTableModel;
    private DefaultTableModel tkdsTableModel;
    private DefaultTableModel spbcTableModel;
    private DefaultTableModel tkhdTableModel;

    private Map<Integer, Brand> mapBrand;

    private final String[] cboTKDTOrderModel = new String[]{"MONTH(B.CREATED_TIME)", "SUM(D.QUANTITY)", "SUM(D.QUANTITY * D.PRICE)"};
    private final String[] cboTKDSOrderModel = new String[]{"E.ID", "E.FULLNAME", "TOTAL_BILL", "TOTAL_ORDER"};
    private final String[] cboSPBCOrderModel = new String[]{"P.ID", "P.NAME", "TOTAL_SOLD"};
    private final String[] cboTKHDOrderModel = new String[]{"MONTH", "TOTAL_BILL", "TOTAL_SOLD", "TOTAL_DELIVERY"};

    public StatisticFrame() {
        initComponents();
        init();
    }

    private void init() {
        brandDAO = new BrandDAO();

        tkdtTableModel = (DefaultTableModel) tblTKDT.getModel();
        tkdsTableModel = (DefaultTableModel) tblTKDS.getModel();
        spbcTableModel = (DefaultTableModel) tblSPBC.getModel();
        tkhdTableModel = (DefaultTableModel) tblTKHD.getModel();

        String[] billYears = JdbcHelper.getListOfArray("SELECT DISTINCT YEAR(B.CREATED_TIME) FROM BILL B ORDER BY YEAR(B.CREATED_TIME) DESC", 1).stream().map(obj -> obj[0]).map(String::valueOf).toArray(String[]::new);
        cboTKDTYear.setModel(new DefaultComboBoxModel<>(billYears));
        cboTKDSYear.setModel(new DefaultComboBoxModel<>(billYears));
        cboSPBCYear.setModel(new DefaultComboBoxModel<>(billYears));
        cboTKHDYear.setModel(new DefaultComboBoxModel<>(billYears));

        mapBrand = brandDAO.selectAll().stream().collect(Collectors.toMap(Brand::getId, Function.identity()));

        DefaultComboBoxModel<Brand> cboSPBCBrandModel = new DefaultComboBoxModel<>();
        cboSPBCBrandModel.addElement(new Brand(0, "Tất Cả"));
        for (Brand brand : mapBrand.values()) {
            cboSPBCBrandModel.addElement(brand);
        }
        cboSPBCBrand.setModel(cboSPBCBrandModel);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cboTKDSMonth.setSelectedIndex(cal.get(Calendar.MONTH));
        cboSPBCMonth.setSelectedIndex(cal.get(Calendar.MONTH));

        loadThongKeDoanhThu();
        loadThongKeDoanhSo();
        loadSanPhamBanChay();
        loadThongKeHoaDon();

    }

    private void loadThongKeDoanhThu() {
        Object year = cboTKDTYear.getSelectedItem();
        String sql = "SELECT\n"
                + "MONTH(B.CREATED_TIME) AS 'Tháng',\n"
                + "SUM(D.QUANTITY) AS 'Tổng Sản Phẩm Bán Được',\n"
                + "SUM(D.QUANTITY * D.PRICE) AS 'Tổng Doanh Thu'\n"
                + "FROM BILL B\n"
                + "INNER JOIN ORDER_DETAIL D\n"
                + "ON B.ORDER_ID = D.ORDER_ID\n"
                + "WHERE YEAR(B.CREATED_TIME) = ?\n"
                + "GROUP BY MONTH(B.CREATED_TIME)\n"
                + "ORDER BY " + cboTKDTOrderModel[cboTKDTOrder.getSelectedIndex()] + " " + (cboTKDTOrderDirection.getSelectedIndex() == 0 ? "DESC" : "ASC");

        List<Object[]> list = JdbcHelper.getListOfArray(sql, 3, year);
        tkdtTableModel.setRowCount(0);
        for (Object[] objects : list) {
            tkdtTableModel.addRow(new Object[]{objects[0], objects[1], MoneyFormat.formatVND(objects[2])});
        }
    }

    private void loadThongKeDoanhSo() {
        Object year = cboTKDSYear.getSelectedItem();
        Object month = cboTKDSMonth.getSelectedItem();
        String sql = "SELECT\n"
                + "E.ID,\n"
                + "E.FULLNAME,\n"
                + "COALESCE(B.TOTAL_BILL, 0) AS 'TOTAL_BILL',\n"
                + "COALESCE(O.TOTAL_ORDER, 0) AS 'TOTAL_ORDER'\n"
                + "FROM EMPLOYEE E\n"
                + "LEFT JOIN (SELECT EMPLOYEE_ID, COUNT(ID) AS 'TOTAL_BILL' FROM BILL WHERE YEAR(CREATED_TIME) = ? AND MONTH(CREATED_TIME) = ? GROUP BY EMPLOYEE_ID) AS B ON E.ID = B.EMPLOYEE_ID\n"
                + "LEFT JOIN (SELECT EMPLOYEE_ID, COUNT(ID) AS 'TOTAL_ORDER' FROM ORDERS WHERE YEAR(CREATED_TIME) = ? AND MONTH(CREATED_TIME) = ? GROUP BY EMPLOYEE_ID) O ON E.ID = O.EMPLOYEE_ID\n"
                + "ORDER BY " + cboTKDSOrderModel[cboTKDSOrder.getSelectedIndex()] + " " + (cboTKDSOrderDirection.getSelectedIndex() == 0 ? "DESC" : "ASC");

        List<Object[]> list = JdbcHelper.getListOfArray(sql, 4, year, month, year, month);
        tkdsTableModel.setRowCount(0);
        for (Object[] objects : list) {
            tkdsTableModel.addRow(objects);
        }
    }

    private void loadSanPhamBanChay() {
        Object year = cboSPBCYear.getSelectedItem();
        Object month = cboSPBCMonth.getSelectedItem();
        String sql = "SELECT\n"
                + "P.ID, P.NAME, P.BRAND_ID, SUM(O.QUANTITY) AS 'TOTAL_SOLD'\n"
                + "FROM ORDER_DETAIL O\n"
                + "INNER JOIN PRODUCT_DETAIL PD\n"
                + "ON O.PRODUCT_DETAIL_ID = PD.ID\n"
                + "INNER JOIN PRODUCT P\n"
                + "ON PD.PRODUCT_ID = P.ID\n"
                + "RIGHT JOIN BILL B\n"
                + "ON O.ORDER_ID = B.ORDER_ID\n"
                + "WHERE YEAR(B.CREATED_TIME) = ? AND MONTH(B.CREATED_TIME) = ?\n"
                + (cboSPBCBrand.getSelectedIndex() > 0 ? " AND P.BRAND_ID = " + ((Brand) cboSPBCBrand.getSelectedItem()).getId() + " \n" : "")
                + "GROUP BY P.ID, P.NAME, P.BRAND_ID\n"
                + "ORDER BY " + cboSPBCOrderModel[cboSPBCOrder.getSelectedIndex()] + " " + (cboSPBCOrderDirection.getSelectedIndex() == 0 ? "DESC" : "ASC");

        List<Object[]> list = JdbcHelper.getListOfArray(sql, 4, year, month);
        spbcTableModel.setRowCount(0);
        for (Object[] objects : list) {
            spbcTableModel.addRow(new Object[]{objects[0], objects[1], mapBrand.get(objects[2]), objects[3]});
        }
    }

    private void loadThongKeHoaDon() {
        Object year = cboTKHDYear.getSelectedItem();
        String sql = "SELECT\n"
                + "MONTH(B.CREATED_TIME) AS 'MONTH',\n"
                + "COUNT(B.ID) AS 'TOTAL_BILL',\n"
                + "COUNT(B.ID) - COUNT(D.ID) AS 'TOTAL_SOLD',\n"
                + "COUNT(D.ID) AS 'TOTAL_DELIVERY'\n"
                + "FROM BILL B\n"
                + "LEFT JOIN DELIVERY D\n"
                + "ON B.ORDER_ID = D.ORDER_ID\n"
                + "WHERE YEAR(B.CREATED_TIME) = ?\n"
                + "GROUP BY MONTH(B.CREATED_TIME)\n"
                + "ORDER BY " + cboTKHDOrderModel[cboTKHDOrder.getSelectedIndex()] + " " + (cboTKHDOrderDirection.getSelectedIndex() == 0 ? "DESC" : "ASC");

        List<Object[]> list = JdbcHelper.getListOfArray(sql, 4, year);
        tkhdTableModel.setRowCount(0);
        for (Object[] objects : list) {
            tkhdTableModel.addRow(objects);
        }
    }

    public void BieuDoSPBC() {
        JpaneSPBC.removeAll();
        Object year = cboSPBCYear.getSelectedItem();
        String sql = "SELECT\n"
                + "P.ID, P.NAME, P.BRAND_ID, CONVERT(varchar,SUM(O.QUANTITY)) AS 'TOTAL_SOLD'\n"
                + "FROM ORDER_DETAIL O\n"
                + "INNER JOIN PRODUCT_DETAIL PD\n"
                + "ON O.PRODUCT_DETAIL_ID = PD.ID\n"
                + "INNER JOIN PRODUCT P\n"
                + "ON PD.PRODUCT_ID = P.ID\n"
                + "INNER JOIN BILL B\n"
                + "ON O.ORDER_ID = B.ORDER_ID\n"
                + "WHERE YEAR(B.CREATED_TIME) = ?\n"
                + "GROUP BY P.ID, P.NAME, P.BRAND_ID";

        List<Object[]> list = JdbcHelper.getListOfArray(sql, 4, year);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (list != null) {
            for (Object[] ob : list) {
                dataset.setValue(Double.parseDouble((String) ob[3]), "Doan Thu", String.valueOf(ob[0]));
            }
            JFreeChart chart = ChartFactory.createBarChart("Biểu Đồ Sản Phẩm Bán Chạy Năm: " + year, "Sản Phẩm", "Value", dataset);
            ChartPanel pn = new ChartPanel(chart);
            pn.setPreferredSize(new Dimension(JpaneSPBC.getWidth(), JpaneSPBC.getHeight()));
            JpaneSPBC.setLayout(new CardLayout());
            JpaneSPBC.add(pn);
            JpaneSPBC.setVisible(true);
            return;
        } else {
            MsgBox.alert(this, year + " Không có dữ liệu");
            return;
        }

    }

    public void BieuDoDoanhSo() {
        jpaneDoanhSo.removeAll();
        Object year = cboTKDSYear.getSelectedItem();
        String sql = "SELECT\n"
                + "E.ID,\n"
                + "E.FULLNAME,\n"
                + "CONVERT(varchar,COALESCE(B.TOTAL_BILL, 0)) AS 'TOTAL_BILL',\n"
                + "CONVERT(varchar,COALESCE(O.TOTAL_ORDER, 0)) AS 'TOTAL_ORDER'\n"
                + "FROM EMPLOYEE E\n"
                + "LEFT JOIN (SELECT EMPLOYEE_ID, CONVERT(varchar,COUNT(ID)) AS 'TOTAL_BILL' \n"
                + "FROM BILL WHERE YEAR(CREATED_TIME) = ? \n"
                + "GROUP BY EMPLOYEE_ID) AS B ON E.ID = B.EMPLOYEE_ID\n"
                + "LEFT JOIN (SELECT EMPLOYEE_ID, CONVERT(varchar,COUNT(ID)) AS 'TOTAL_ORDER' \n"
                + "FROM ORDERS WHERE YEAR(CREATED_TIME) = ? \n"
                + "GROUP BY EMPLOYEE_ID) O ON E.ID = O.EMPLOYEE_ID";
        List<Object[]> list = JdbcHelper.getListOfArray(sql, 4, year, year);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (list != null) {
            for (Object[] ob : list) {
                dataset.setValue(Double.parseDouble((String) ob[3]), "Tại Cửa Hàng", String.valueOf(ob[1]));
                dataset.setValue(Double.parseDouble((String) ob[2]), "Đặt Hàng", String.valueOf(ob[1]));
            }
            JFreeChart chart = ChartFactory.createBarChart("Biểu Đồ Doanh Số Nhân Viên Bán Tại Cửa Hàng và Đặt Hàng Trong Năm: " + year + "", "Sản Phẩm", "Value", dataset);
            ChartPanel pn = new ChartPanel(chart);
            pn.setPreferredSize(new Dimension(jpaneDoanhSo.getWidth(), jpaneDoanhSo.getHeight()));
            jpaneDoanhSo.setLayout(new CardLayout());
            jpaneDoanhSo.add(pn);
            jpaneDoanhSo.setVisible(true);
            return;
        } else {
            MsgBox.alert(this, year + " Không có dữ liệu");
            return;
        }
    }

    public void BieuDoDoanhThu() {
        Jpane0.removeAll();
        Object year = cboTKDTYear.getSelectedItem();
        String sql = "SELECT\n"
                + "MONTH(B.CREATED_TIME) AS 'Tháng',\n"
                + "SUM(D.QUANTITY) AS 'Tổng Sản Phẩm Bán Được',\n"
                + "CONVERT(varchar,SUM(D.QUANTITY * D.PRICE))  AS 'Tổng Doanh Thu'\n"
                + "FROM BILL B\n"
                + "INNER JOIN ORDER_DETAIL D\n"
                + "ON B.ORDER_ID = D.ORDER_ID\n"
                + "where YEAR(B.CREATED_TIME) = ?\n"
                + "GROUP BY MONTH(B.CREATED_TIME)";

        List<Object[]> list = JdbcHelper.getListOfArray(sql, 3, year);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (list != null) {
            for (Object[] ob : list) {
                dataset.setValue(Double.parseDouble((String) ob[2]), "Doan Thu", String.valueOf(ob[0]));
            }
            JFreeChart chart = ChartFactory.createBarChart("Biểu Đồ Doanh Thu Năm: " + year, "Tháng", "Value", dataset);
            NumberAxis rangeAxis = (NumberAxis) chart.getCategoryPlot().getRangeAxis();
            rangeAxis.setNumberFormatOverride(new DecimalFormat());
            ChartPanel pn = new ChartPanel(chart);
            pn.setPreferredSize(new Dimension(Jpane0.getWidth(), Jpane0.getHeight()));
            Jpane0.add(pn);
            Jpane0.setLayout(new CardLayout());
            Jpane0.setVisible(true);
            return;
        } else {
            MsgBox.alert(this, year + " Không có dữ liệu");
            return;
        }
    }

    public void BieuDoHoaDon() {
        JpaneHoaDon.removeAll();
        Object year = cboTKHDYear.getSelectedItem();
        String sql = "SELECT\n"
                + "CONVERT(varchar,MONTH(B.CREATED_TIME)) AS 'MONTH',\n"
                + "COUNT(B.ID) AS 'TOTAL_BILL',\n"
                + "CONVERT(varchar,COUNT(B.ID) - COUNT(D.ID)) AS 'TOTAL_SOLD',\n"
                + "CONVERT(varchar,COUNT(D.ID)) AS 'TOTAL_DELIVERY'\n"
                + "FROM BILL B\n"
                + "LEFT JOIN DELIVERY D\n"
                + "ON B.ORDER_ID = D.ORDER_ID\n"
                + "where YEAR(B.CREATED_TIME) = ? \n"
                + "GROUP BY MONTH(B.CREATED_TIME)";
        List<Object[]> list = JdbcHelper.getListOfArray(sql, 4, year);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (list != null) {
            for (Object[] ob : list) {
                dataset.setValue(Double.parseDouble((String) ob[2]), "Đơn Mua Tại Shop", String.valueOf(ob[0]));
                dataset.setValue(Double.parseDouble((String) ob[3]), "Đơn Giao Hàng", String.valueOf(ob[0]));
            }
            JFreeChart chart = ChartFactory.createBarChart("Biểu Đồ Thể Hiện Tổng Số Đơn Hàng Qua Các Tháng Trong Năm: " + year, "Tháng", "Value", dataset);
            ChartPanel pn = new ChartPanel(chart);
            pn.setPreferredSize(new Dimension(JpaneHoaDon.getWidth(), JpaneHoaDon.getHeight()));
            JpaneHoaDon.add(pn);
            JpaneHoaDon.setLayout(new CardLayout());
            JpaneHoaDon.setVisible(true);
            return;
        } else {
            MsgBox.alert(this, year + " Không có dữ liệu");
            return;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboTKDTYear = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTKDT = new javax.swing.JTable();
        cboTKDTOrder = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        cboTKDTOrderDirection = new javax.swing.JComboBox<>();
        Jpane0 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTKDS = new javax.swing.JTable();
        cboTKDSYear = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cboTKDSOrder = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cboTKDSOrderDirection = new javax.swing.JComboBox<>();
        cboTKDSMonth = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jpaneDoanhSo = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblSPBC = new javax.swing.JTable();
        cboSPBCYear = new javax.swing.JComboBox<>();
        cboSPBCMonth = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cboSPBCOrder = new javax.swing.JComboBox<>();
        cboSPBCOrderDirection = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        cboSPBCBrand = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        JpaneSPBC = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblTKHD = new javax.swing.JTable();
        cboTKHDYear = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        cboTKHDOrder = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        cboTKHDOrderDirection = new javax.swing.JComboBox<>();
        JpaneHoaDon = new javax.swing.JPanel();

        setClosable(true);
        setIconifiable(true);
        setTitle("ShoeStoreSys - Thống Kê");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logoExtraSmall.jpg"))); // NOI18N
        setPreferredSize(new java.awt.Dimension(1200, 800));

        jTabbedPane3.setFocusable(false);
        jTabbedPane3.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel1.setText("Năm");

        cboTKDTYear.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboTKDTYear.setFocusable(false);
        cboTKDTYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTKDTYearActionPerformed(evt);
            }
        });
        cboTKDTYear.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cboTKDTYearPropertyChange(evt);
            }
        });

        tblTKDT.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblTKDT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tháng", "Tổng Sản Phẩm Bán Được", "Doanh Thu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTKDT.setRowHeight(30);
        tblTKDT.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblTKDT.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblTKDT);

        cboTKDTOrder.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboTKDTOrder.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tháng", "Tổng Sản Phẩm Bán Được", "Doanh Thu" }));
        cboTKDTOrder.setFocusable(false);
        cboTKDTOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTKDTOrderActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel2.setText("Sắp Xếp Theo");

        cboTKDTOrderDirection.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboTKDTOrderDirection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Giảm Dần", "Tăng Dần" }));
        cboTKDTOrderDirection.setFocusable(false);
        cboTKDTOrderDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTKDTOrderDirectionActionPerformed(evt);
            }
        });

        Jpane0.setBackground(new java.awt.Color(255, 255, 0));
        Jpane0.setPreferredSize(new java.awt.Dimension(450, 450));
        Jpane0.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(cboTKDTYear, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboTKDTOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboTKDTOrderDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(78, 78, 78))
                    .addComponent(Jpane0, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 737, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTKDTYear, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(cboTKDTOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTKDTOrderDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(Jpane0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(162, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Doanh thu", jPanel1);

        tblTKDS.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblTKDS.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Nhân Viên", "Tên Nhân Viên", "Tổng Số Đơn Thanh Toán", "Tổng Số Đơn Đặt Hàng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTKDS.setRowHeight(30);
        tblTKDS.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblTKDS.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblTKDS);

        cboTKDSYear.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboTKDSYear.setFocusable(false);
        cboTKDSYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTKDSYearActionPerformed(evt);
            }
        });
        cboTKDSYear.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cboTKDSYearPropertyChange(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel3.setText("Năm");

        cboTKDSOrder.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboTKDSOrder.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mã Nhân Viên", "Tên Nhân Viên", "Tổng Số Đơn Thanh Toán", "Tổng Số Đơn Đặt Hàng" }));
        cboTKDSOrder.setFocusable(false);
        cboTKDSOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTKDSOrderActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel4.setText("Sắp Xếp Theo");

        cboTKDSOrderDirection.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboTKDSOrderDirection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Giảm Dần", "Tăng Dần" }));
        cboTKDSOrderDirection.setFocusable(false);
        cboTKDSOrderDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTKDSOrderDirectionActionPerformed(evt);
            }
        });

        cboTKDSMonth.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboTKDSMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        cboTKDSMonth.setFocusable(false);
        cboTKDSMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTKDSMonthActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel5.setText("Tháng");

        jpaneDoanhSo.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 487, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jpaneDoanhSo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cboTKDSYear, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(cboTKDSMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(cboTKDSOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboTKDSOrderDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTKDSYear, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cboTKDSOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTKDSOrderDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTKDSMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jpaneDoanhSo, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Doanh số", jPanel2);

        tblSPBC.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblSPBC.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Sản Phẩm", "Tên Sản Phẩm", "Hãng", "Số Lượng Bán Được"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSPBC.setRowHeight(30);
        tblSPBC.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblSPBC.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tblSPBC);

        cboSPBCYear.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboSPBCYear.setFocusable(false);
        cboSPBCYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSPBCYearActionPerformed(evt);
            }
        });
        cboSPBCYear.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cboSPBCYearPropertyChange(evt);
            }
        });

        cboSPBCMonth.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboSPBCMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        cboSPBCMonth.setFocusable(false);
        cboSPBCMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSPBCMonthActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel6.setText("Tháng");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel7.setText("Sắp Xếp Theo");

        cboSPBCOrder.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboSPBCOrder.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mã Sản Phẩm", "Tên Sản Phẩm", "Số Lượng Bán Được" }));
        cboSPBCOrder.setFocusable(false);
        cboSPBCOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSPBCOrderActionPerformed(evt);
            }
        });

        cboSPBCOrderDirection.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboSPBCOrderDirection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Giảm Dần", "Tăng Dần" }));
        cboSPBCOrderDirection.setFocusable(false);
        cboSPBCOrderDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSPBCOrderDirectionActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel8.setText("Năm");

        cboSPBCBrand.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboSPBCBrand.setFocusable(false);
        cboSPBCBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSPBCBrandActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel9.setText("Hãng");

        JpaneSPBC.setBackground(new java.awt.Color(255, 0, 0));
        JpaneSPBC.setPreferredSize(new java.awt.Dimension(450, 450));
        JpaneSPBC.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(cboSPBCYear, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(cboSPBCMonth, 0, 94, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(cboSPBCBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(cboSPBCOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboSPBCOrderDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(JpaneSPBC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboSPBCYear, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboSPBCOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboSPBCOrderDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboSPBCMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8)
                    .addComponent(cboSPBCBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(JpaneSPBC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Sản phẩm bán chạy", jPanel3);

        tblTKHD.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblTKHD.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tháng", "Tổng Số Đơn Hàng", "Đơn Mua Tại Shop", "Đơn Giao Hàng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTKHD.setRowHeight(30);
        tblTKHD.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblTKHD.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tblTKHD);

        cboTKHDYear.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboTKHDYear.setFocusable(false);
        cboTKHDYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTKHDYearActionPerformed(evt);
            }
        });
        cboTKHDYear.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cboTKHDYearPropertyChange(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel12.setText("Năm");

        cboTKHDOrder.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboTKHDOrder.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tháng", "Tổng Số Đơn Hàng", "Đơn Mua Tại Shop", "Đơn Giao Hàng" }));
        cboTKHDOrder.setFocusable(false);
        cboTKHDOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTKHDOrderActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel15.setText("Sắp Xếp Theo");

        cboTKHDOrderDirection.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboTKHDOrderDirection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Giảm Dần", "Tăng Dần" }));
        cboTKHDOrderDirection.setFocusable(false);
        cboTKHDOrderDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTKHDOrderDirectionActionPerformed(evt);
            }
        });

        JpaneHoaDon.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(JpaneHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(cboTKHDYear, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(cboTKHDOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboTKHDOrderDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTKHDYear, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(cboTKHDOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTKHDOrderDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(JpaneHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Hóa đơn", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboTKDTYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTKDTYearActionPerformed
        loadThongKeDoanhThu();
    }//GEN-LAST:event_cboTKDTYearActionPerformed

    private void cboTKDTOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTKDTOrderActionPerformed
        loadThongKeDoanhThu();
    }//GEN-LAST:event_cboTKDTOrderActionPerformed

    private void cboTKDTOrderDirectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTKDTOrderDirectionActionPerformed
        loadThongKeDoanhThu();
    }//GEN-LAST:event_cboTKDTOrderDirectionActionPerformed

    private void cboTKDSYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTKDSYearActionPerformed
        loadThongKeDoanhSo();
    }//GEN-LAST:event_cboTKDSYearActionPerformed

    private void cboTKDSOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTKDSOrderActionPerformed
        loadThongKeDoanhSo();
    }//GEN-LAST:event_cboTKDSOrderActionPerformed

    private void cboTKDSOrderDirectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTKDSOrderDirectionActionPerformed
        loadThongKeDoanhSo();
    }//GEN-LAST:event_cboTKDSOrderDirectionActionPerformed

    private void cboTKDSMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTKDSMonthActionPerformed
        loadThongKeDoanhSo();
    }//GEN-LAST:event_cboTKDSMonthActionPerformed

    private void cboSPBCYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSPBCYearActionPerformed
        loadSanPhamBanChay();
    }//GEN-LAST:event_cboSPBCYearActionPerformed

    private void cboSPBCMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSPBCMonthActionPerformed
        loadSanPhamBanChay();
    }//GEN-LAST:event_cboSPBCMonthActionPerformed

    private void cboSPBCOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSPBCOrderActionPerformed
        loadSanPhamBanChay();
    }//GEN-LAST:event_cboSPBCOrderActionPerformed

    private void cboSPBCOrderDirectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSPBCOrderDirectionActionPerformed
        loadSanPhamBanChay();
    }//GEN-LAST:event_cboSPBCOrderDirectionActionPerformed

    private void cboSPBCBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSPBCBrandActionPerformed
        loadSanPhamBanChay();
    }//GEN-LAST:event_cboSPBCBrandActionPerformed

    private void cboTKHDYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTKHDYearActionPerformed
        loadThongKeHoaDon();
    }//GEN-LAST:event_cboTKHDYearActionPerformed

    private void cboTKHDOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTKHDOrderActionPerformed
        loadThongKeHoaDon();
    }//GEN-LAST:event_cboTKHDOrderActionPerformed

    private void cboTKHDOrderDirectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTKHDOrderDirectionActionPerformed
        loadThongKeHoaDon();
    }//GEN-LAST:event_cboTKHDOrderDirectionActionPerformed

    private void cboTKHDYearPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cboTKHDYearPropertyChange
        BieuDoHoaDon();
    }//GEN-LAST:event_cboTKHDYearPropertyChange

    private void cboSPBCYearPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cboSPBCYearPropertyChange
        BieuDoSPBC();
    }//GEN-LAST:event_cboSPBCYearPropertyChange

    private void cboTKDSYearPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cboTKDSYearPropertyChange
        BieuDoDoanhSo();
    }//GEN-LAST:event_cboTKDSYearPropertyChange

    private void cboTKDTYearPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cboTKDTYearPropertyChange
        BieuDoDoanhThu();
    }//GEN-LAST:event_cboTKDTYearPropertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Jpane0;
    private javax.swing.JPanel JpaneHoaDon;
    private javax.swing.JPanel JpaneSPBC;
    private javax.swing.JComboBox<Brand> cboSPBCBrand;
    private javax.swing.JComboBox<String> cboSPBCMonth;
    private javax.swing.JComboBox<String> cboSPBCOrder;
    private javax.swing.JComboBox<String> cboSPBCOrderDirection;
    private javax.swing.JComboBox<String> cboSPBCYear;
    private javax.swing.JComboBox<String> cboTKDSMonth;
    private javax.swing.JComboBox<String> cboTKDSOrder;
    private javax.swing.JComboBox<String> cboTKDSOrderDirection;
    private javax.swing.JComboBox<String> cboTKDSYear;
    private javax.swing.JComboBox<String> cboTKDTOrder;
    private javax.swing.JComboBox<String> cboTKDTOrderDirection;
    private javax.swing.JComboBox<String> cboTKDTYear;
    private javax.swing.JComboBox<String> cboTKHDOrder;
    private javax.swing.JComboBox<String> cboTKHDOrderDirection;
    private javax.swing.JComboBox<String> cboTKHDYear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JPanel jpaneDoanhSo;
    private javax.swing.JTable tblSPBC;
    private javax.swing.JTable tblTKDS;
    private javax.swing.JTable tblTKDT;
    private javax.swing.JTable tblTKHD;
    // End of variables declaration//GEN-END:variables
}
