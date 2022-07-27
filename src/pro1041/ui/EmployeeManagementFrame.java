package pro1041.ui;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import pro1041.dao.EmployeeDAO;
import pro1041.entity.Employee;
import pro1041.entity.Role;
import pro1041.utils.Auth;
import pro1041.utils.JdbcHelper;
import pro1041.utils.MsgBox;
import pro1041.utils.Validator;

public class EmployeeManagementFrame extends javax.swing.JInternalFrame {

    private EmployeeDAO employeeDAO;

    private List<Employee> listEmployee;
    private DefaultTableModel employeeTableModel;

    int index;

    private final String[] cboActivateFilterModel = {"", " AND ACTIVATE = 1", " AND ACTIVATE = 0"};

    public EmployeeManagementFrame() {
        initComponents();
        init();
    }

    private void init() {
        employeeDAO = new EmployeeDAO();
        listEmployee = employeeDAO.selectAll();
        employeeTableModel = (DefaultTableModel) tblEmployee.getModel();
        tblEmployee.getTableHeader().setFont(new java.awt.Font("Tahoma", 1, 16));

        DefaultComboBoxModel<String> cboModel = new DefaultComboBoxModel<>();
        cboModel.addElement("Tất Cả");
        for (Role value : Role.values()) {
            cboModel.addElement(value.toString());
        }
        cboRoleFilter.setModel(cboModel);

        fillToTable(listEmployee);
        updateState();

        tblEmployee.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            updateIndexStatus();
        });
    }

    private void clear() {
        txtID.setText("");
        txtFullname.setText("");
        txtUsername.setText("");
        txtPhoneNumber.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        rdoEmployee.setSelected(true);
        rdoMale.setSelected(true);
        rdoActivate.setSelected(true);
    }

    private void updateState() {
        boolean edit = index > -1;
        boolean first = index == 0;
        boolean last = index == tblEmployee.getRowCount() - 1;

        btnSave.setEnabled(!edit);
        btnUpdate.setEnabled(edit);
        btnChangePassword.setEnabled(edit);

        btnFirst.setEnabled(edit && !first);
        btnPre.setEnabled(edit && !first);
        btnNext.setEnabled(edit && !last);
        btnLast.setEnabled(edit && !last);
    }

    private void showDetail(Employee employee) {
        txtID.setText(String.valueOf(employee.getId()));
        txtFullname.setText(employee.getFullname());
        txtUsername.setText(employee.getUsername());
        txtPhoneNumber.setText(employee.getPhoneNumber());
        txtEmail.setText(employee.getEmail());
        txtAddress.setText(employee.getAddress());
        if (employee.getRole() == Role.MANAGER) {
            rdoManager.setSelected(true);
        } else {
            rdoEmployee.setSelected(true);
        }
        if (employee.isGender()) {
            rdoMale.setSelected(true);
        } else {
            rdoFemale.setSelected(true);
        }
        if (employee.isActivate()) {
            rdoActivate.setSelected(true);
        } else {
            rdoDeactivate.setSelected(true);
        }
    }

    private void updateIndexStatus() {
        lblIndex.setText((tblEmployee.getSelectedRow() + 1) + " / " + listEmployee.size());
    }

    private void fillToTable(Employee employee) {
        employeeTableModel.addRow(new Object[]{employee.getId(), employee.getFullname(), employee.getUsername(), employee.getRole().toString(), employee.isGender() ? "Nam" : "Nữ", employee.getEmail(), employee.getPhoneNumber(), employee.getAddress(), employee.isActivate() ? "Hoạt Động" : "Không Hoạt Động"});
    }

    private void fillToTable(int index, Employee employee) {
        employeeTableModel.setValueAt(employee.getFullname(), index, 1);
        employeeTableModel.setValueAt(employee.getUsername(), index, 2);
        employeeTableModel.setValueAt(employee.getRole().toString(), index, 3);
        employeeTableModel.setValueAt(employee.isGender() ? "Nam" : "Nữ", index, 4);
        employeeTableModel.setValueAt(employee.getEmail(), index, 5);
        employeeTableModel.setValueAt(employee.getPhoneNumber(), index, 6);
        employeeTableModel.setValueAt(employee.getAddress(), index, 7);
        employeeTableModel.setValueAt(employee.isActivate() ? "Hoạt Động" : "Không Hoạt Động", index, 8);
    }

    private void fillToTable(List<Employee> list) {
        employeeTableModel.setRowCount(0);
        for (Employee employee : list) {
            fillToTable(employee);
        }
        index = -1;
        updateIndexStatus();
    }

    private Employee getEmployeeByForm() {
        if (Validator.isNull(txtFullname, "Vui lòng nhập tên nhân viên")
                || Validator.isNull(txtUsername, "Vui lòng nhập tên đăng nhập nhân viên")
                || Validator.isNull(txtEmail, "Vui lòng nhập email nhân viên")
                || Validator.isNull(txtPhoneNumber, "Vui lòng nhập số điện thoại nhân viên")
                || Validator.isContainSpecialCharacter(txtUsername, "Tên đăng nhập không được chứa kí tự đặc biệt, vui lòng thử lại !")
                || Validator.isValidEmail(txtEmail, "Vui lòng nhập email đúng định dạng")
                || Validator.isValidPhone(txtPhoneNumber, "Số điện thoại không hợp lệ")) {
            return null;
        }

        Role role = rdoManager.isSelected() ? Role.MANAGER : Role.EMPLOYEE;

        return new Employee(txtUsername.getText(), null, txtFullname.getText(), role, rdoMale.isSelected(), txtEmail.getText(), txtPhoneNumber.getText(), txtAddress.getText(), rdoActivate.isSelected());
    }

    private void searchAllEmployee() {
        listEmployee = employeeDAO.selectAll();
        fillToTable(listEmployee);
        updateState();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        palListEmployee = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblEmployee = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        cboRoleFilter = new javax.swing.JComboBox<>();
        btnShowAll = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        cboActivateFilter = new javax.swing.JComboBox<>();
        lblIndex = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        palEmployee = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        rdoManager = new javax.swing.JRadioButton();
        rdoEmployee = new javax.swing.JRadioButton();
        rdoMale = new javax.swing.JRadioButton();
        rdoFemale = new javax.swing.JRadioButton();
        txtID = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtFullname = new javax.swing.JTextField();
        txtUsername = new javax.swing.JTextField();
        txtPhoneNumber = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAddress = new javax.swing.JTextArea();
        btnSave = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnPre = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        rdoActivate = new javax.swing.JRadioButton();
        rdoDeactivate = new javax.swing.JRadioButton();
        btnChangePassword = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("ShoeStoreSys - Quản Lý Nhân Viên");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logoExtraSmall.jpg"))); // NOI18N
        setPreferredSize(new java.awt.Dimension(1200, 800));

        jTabbedPane1.setFocusable(false);
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jTabbedPane1.setMaximumSize(new java.awt.Dimension(1000, 800));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(1000, 800));

        tblEmployee.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblEmployee.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Tên", "Tên Đăng Nhập", "Chức Vụ", "Giới Tính", "Email", "Số Điện Thoại", "Địa Chỉ", "Trạng Thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblEmployee.setRowHeight(30);
        tblEmployee.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblEmployee.getTableHeader().setReorderingAllowed(false);
        tblEmployee.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblEmployeeMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblEmployee);

        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtSearch.setMargin(new java.awt.Insets(2, 15, 2, 2));

        btnSearch.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/search-client-30.png"))); // NOI18N
        btnSearch.setText("Tìm Kiếm");
        btnSearch.setFocusable(false);
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        cboRoleFilter.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboRoleFilter.setFocusable(false);

        btnShowAll.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnShowAll.setText("Hiển Thị Tất Cả");
        btnShowAll.setFocusable(false);
        btnShowAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowAllActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel12.setText("Chức Vụ");

        cboActivateFilter.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        cboActivateFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất Cả", "Hoạt Động", "Không Hoạt Động" }));
        cboActivateFilter.setFocusable(false);

        lblIndex.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblIndex.setForeground(new java.awt.Color(255, 102, 102));
        lblIndex.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIndex.setText("Index");
        lblIndex.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel13.setText("Trạng Thái");

        javax.swing.GroupLayout palListEmployeeLayout = new javax.swing.GroupLayout(palListEmployee);
        palListEmployee.setLayout(palListEmployeeLayout);
        palListEmployeeLayout.setHorizontalGroup(
            palListEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(palListEmployeeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(palListEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palListEmployeeLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboRoleFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboActivateFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnShowAll, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palListEmployeeLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        palListEmployeeLayout.setVerticalGroup(
            palListEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(palListEmployeeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(palListEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboRoleFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnShowAll, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(cboActivateFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        palListEmployeeLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnSearch, btnShowAll, cboActivateFilter, cboRoleFilter, txtSearch});

        jTabbedPane1.addTab("Danh Sách", palListEmployee);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Mã nhân viên");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Tên nhân viên");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Tên tài khoản");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Chức vụ");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Giới tính");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Số điện thoại");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("E-mail");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Địa Chỉ");

        buttonGroup1.add(rdoManager);
        rdoManager.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rdoManager.setText("Quản lý");
        rdoManager.setFocusable(false);

        buttonGroup1.add(rdoEmployee);
        rdoEmployee.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rdoEmployee.setSelected(true);
        rdoEmployee.setText("Nhân viên");
        rdoEmployee.setFocusable(false);

        buttonGroup2.add(rdoMale);
        rdoMale.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rdoMale.setSelected(true);
        rdoMale.setText("Nam");
        rdoMale.setFocusable(false);

        buttonGroup2.add(rdoFemale);
        rdoFemale.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rdoFemale.setText("Nữ");
        rdoFemale.setFocusable(false);

        txtID.setEditable(false);
        txtID.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtID.setText(" ");

        txtEmail.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        txtFullname.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        txtUsername.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        txtPhoneNumber.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        txtAddress.setColumns(20);
        txtAddress.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtAddress.setRows(5);
        jScrollPane3.setViewportView(txtAddress);

        btnSave.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/add-user-male-30.png"))); // NOI18N
        btnSave.setText("Thêm Mới");
        btnSave.setFocusable(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/update-user-skin-type-7-40.png"))); // NOI18N
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

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 40)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 153, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("THÔNG TIN NHÂN VIÊN");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 51, 51));
        jLabel11.setText("Trạng Thái");

        buttonGroup3.add(rdoActivate);
        rdoActivate.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rdoActivate.setForeground(new java.awt.Color(255, 51, 51));
        rdoActivate.setSelected(true);
        rdoActivate.setText("Hoạt Động");
        rdoActivate.setFocusable(false);

        buttonGroup3.add(rdoDeactivate);
        rdoDeactivate.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rdoDeactivate.setForeground(new java.awt.Color(255, 51, 51));
        rdoDeactivate.setText("Ngưng Hoạt Động");
        rdoDeactivate.setFocusable(false);

        btnChangePassword.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnChangePassword.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/password-reset-30.png"))); // NOI18N
        btnChangePassword.setText(" Đổi Mật Khẩu");
        btnChangePassword.setFocusable(false);
        btnChangePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangePasswordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout palEmployeeLayout = new javax.swing.GroupLayout(palEmployee);
        palEmployee.setLayout(palEmployeeLayout);
        palEmployeeLayout.setHorizontalGroup(
            palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(palEmployeeLayout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(palEmployeeLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 988, Short.MAX_VALUE))
                    .addGroup(palEmployeeLayout.createSequentialGroup()
                        .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(palEmployeeLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(72, 72, 72)
                                .addComponent(txtUsername))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palEmployeeLayout.createSequentialGroup()
                                .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addGap(68, 68, 68)
                                .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtID, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                                    .addComponent(txtFullname)
                                    .addGroup(palEmployeeLayout.createSequentialGroup()
                                        .addComponent(rdoActivate)
                                        .addGap(68, 68, 68)
                                        .addComponent(rdoDeactivate))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(palEmployeeLayout.createSequentialGroup()
                                    .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel5)
                                        .addComponent(jLabel7)
                                        .addComponent(jLabel9))
                                    .addGap(77, 77, 77)
                                    .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtPhoneNumber)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(palEmployeeLayout.createSequentialGroup()
                                            .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(rdoManager)
                                                .addComponent(rdoMale))
                                            .addGap(68, 68, 68)
                                            .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(rdoFemale)
                                                .addComponent(rdoEmployee)))))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palEmployeeLayout.createSequentialGroup()
                                    .addComponent(jLabel8)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtEmail))))
                        .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(palEmployeeLayout.createSequentialGroup()
                                .addGap(109, 109, 109)
                                .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnReset)
                                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnChangePassword)
                                    .addComponent(btnUpdate))
                                .addGap(20, 20, 20))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palEmployeeLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnFirst, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnPre, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(59, 59, 59))))))
        );

        palEmployeeLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnChangePassword, btnReset, btnSave, btnUpdate});

        palEmployeeLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtEmail, txtFullname, txtID, txtPhoneNumber, txtUsername});

        palEmployeeLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnFirst, btnLast, btnNext, btnPre});

        palEmployeeLayout.setVerticalGroup(
            palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, palEmployeeLayout.createSequentialGroup()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rdoActivate)
                        .addComponent(rdoDeactivate))
                    .addComponent(jLabel11))
                .addGap(22, 22, 22)
                .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(palEmployeeLayout.createSequentialGroup()
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnChangePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnPre))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLast)))
                    .addGroup(palEmployeeLayout.createSequentialGroup()
                        .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdoManager)
                            .addComponent(rdoEmployee)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdoMale)
                            .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(rdoFemale)
                                .addComponent(jLabel6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGroup(palEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(palEmployeeLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(palEmployeeLayout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(67, 67, 67))
        );

        palEmployeeLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnChangePassword, btnReset, btnSave, btnUpdate});

        palEmployeeLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtEmail, txtFullname, txtID, txtPhoneNumber, txtUsername});

        palEmployeeLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFirst, btnLast, btnNext, btnPre});

        jTabbedPane1.addTab("Cập Nhật", palEmployee);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1184, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 764, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblEmployeeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblEmployeeMouseReleased
        if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2) {
            index = tblEmployee.getSelectedRow();
            showDetail(listEmployee.get(index));
            updateState();
            jTabbedPane1.setSelectedIndex(1);
        }
    }//GEN-LAST:event_tblEmployeeMouseReleased

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        Employee employee = getEmployeeByForm();
        if (employee == null) {
            return;
        }

        if (JdbcHelper.getListOfArray("SELECT ID FROM EMPLOYEE WHERE USERNAME = ?", 1, employee.getUsername()).size() > 0) {
            MsgBox.alert(null, "Tên đăng nhập đã có người sử dụng, vui lòng nhập tên khác !");
            txtUsername.requestFocus();
            return;
        }

        if (JdbcHelper.getListOfArray("SELECT ID FROM EMPLOYEE WHERE EMAIL = ?", 1, employee.getEmail()).size() > 0) {
            MsgBox.alert(null, "Email đã có người sử dụng, vui lòng nhập email khác !");
            txtEmail.requestFocus();
            return;
        }

        if (JdbcHelper.getListOfArray("SELECT ID FROM EMPLOYEE WHERE PHONE_NUMBER = ?", 1, employee.getPhoneNumber()).size() > 0) {
            MsgBox.alert(null, "Số điện thoại đã có người sử dụng, vui lòng nhập số điện thoại khác !");
            txtPhoneNumber.requestFocus();
            return;
        }

        ConfirmPasswordDialog confirmPasswordDialog = new ConfirmPasswordDialog(null, true);
        confirmPasswordDialog.setVisible(true);
        String password = confirmPasswordDialog.getPassword();
        if (password == null) {
            return;
        }
        employee.setPassword(Auth.getSecurePassword(password));
        employeeDAO.insert(employee);
        listEmployee.add(employee);
        fillToTable(employee);
        txtID.setText(String.valueOf(employee.getId()));
        MsgBox.alert(this, "Thêm nhân viên thành công !");
        index = listEmployee.size() - 1;
        tblEmployee.setRowSelectionInterval(index, index);
        updateState();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        Employee employee = getEmployeeByForm();
        if (employee == null) {
            return;
        }
        if (listEmployee.get(index).getId() == Auth.getUser().getId()) {
            if (!employee.isActivate()) {
                MsgBox.alert(this, "Bạn không thể hủy kích hoạt bản thân");
                return;
            }

            if (employee.getRole() != Auth.getUser().getRole()) {
                MsgBox.alert(this, "Bạn không thể đổi chức vụ bản thân");
                return;
            }
        }
        employee.setId(listEmployee.get(index).getId());
        employee.setPassword(listEmployee.get(index).getPassword());

        if (JdbcHelper.getListOfArray("SELECT ID FROM EMPLOYEE WHERE USERNAME = ? AND ID != ?", 1, employee.getUsername(), employee.getId()).size() > 0) {
            MsgBox.alert(null, "Tên đăng nhập đã có người sử dụng, vui lòng nhập tên khác !");
            txtUsername.requestFocus();
            return;
        }

        if (JdbcHelper.getListOfArray("SELECT ID FROM EMPLOYEE WHERE EMAIL = ? AND ID != ?", 1, employee.getEmail(), employee.getId()).size() > 0) {
            MsgBox.alert(null, "Email đã có người sử dụng, vui lòng nhập email khác !");
            txtEmail.requestFocus();
            return;
        }

        if (JdbcHelper.getListOfArray("SELECT ID FROM EMPLOYEE WHERE PHONE_NUMBER = ? AND ID != ?", 1, employee.getPhoneNumber(), employee.getId()).size() > 0) {
            MsgBox.alert(null, "Số điện thoại đã có người sử dụng, vui lòng nhập số khác !");
            txtPhoneNumber.requestFocus();
            return;
        }

        employeeDAO.update(employee);
        listEmployee.set(index, employee);
        fillToTable(index, employee);
        tblEmployee.setRowSelectionInterval(index, index);
        MsgBox.alert(this, "Cập nhật nhân viên thành công !");
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        clear();
        tblEmployee.clearSelection();
        index = -1;
        updateState();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        index = 0;
        tblEmployee.setRowSelectionInterval(0, 0);
        showDetail(listEmployee.get(0));
        updateState();
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreActionPerformed
        index--;
        tblEmployee.setRowSelectionInterval(index, index);
        showDetail(listEmployee.get(index));
        updateState();
    }//GEN-LAST:event_btnPreActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        index++;
        tblEmployee.setRowSelectionInterval(index, index);
        showDetail(listEmployee.get(index));
        updateState();
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        index = tblEmployee.getRowCount() - 1;
        tblEmployee.setRowSelectionInterval(index, index);
        showDetail(listEmployee.get(index));
        updateState();
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowAllActionPerformed
        txtSearch.setText("");
        cboRoleFilter.setSelectedIndex(0);
        cboActivateFilter.setSelectedIndex(0);
        searchAllEmployee();
    }//GEN-LAST:event_btnShowAllActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String search = "%" + txtSearch.getText() + "%";
        String sql = "SELECT * FROM EMPLOYEE WHERE ( ID LIKE ? OR USERNAME LIKE ? OR FULLNAME LIKE ? OR EMAIL LIKE ? OR PHONE_NUMBER LIKE ? )";
        sql += cboActivateFilterModel[cboActivateFilter.getSelectedIndex()];

        if (cboRoleFilter.getSelectedIndex() != 0) {
            sql += " AND ROLE = '" + cboRoleFilter.getSelectedItem().toString() + "'";
        }

        listEmployee = employeeDAO.selectBySQL(sql, search, search, search, search, search);
        fillToTable(listEmployee);
        updateState();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnChangePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangePasswordActionPerformed
        ConfirmPasswordDialog confirmPasswordDialog = new ConfirmPasswordDialog(null, true);
        confirmPasswordDialog.setVisible(true);
        String password = confirmPasswordDialog.getPassword();
        if (password == null) {
            return;
        }
        Employee employee = listEmployee.get(index);
        employee.setPassword(Auth.getSecurePassword(password));
        employeeDAO.update(employee);
        MsgBox.alert(null, "Đổi mật khẩu thành công !");
    }//GEN-LAST:event_btnChangePasswordActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChangePassword;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPre;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnShowAll;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JComboBox<String> cboActivateFilter;
    private javax.swing.JComboBox<String> cboRoleFilter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblIndex;
    private javax.swing.JPanel palEmployee;
    private javax.swing.JPanel palListEmployee;
    private javax.swing.JRadioButton rdoActivate;
    private javax.swing.JRadioButton rdoDeactivate;
    private javax.swing.JRadioButton rdoEmployee;
    private javax.swing.JRadioButton rdoFemale;
    private javax.swing.JRadioButton rdoMale;
    private javax.swing.JRadioButton rdoManager;
    private javax.swing.JTable tblEmployee;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFullname;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtPhoneNumber;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
