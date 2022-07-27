package pro1041.ui;

import pro1041.dao.EmployeeDAO;
import pro1041.entity.Employee;
import pro1041.entity.Role;
import pro1041.utils.Auth;
import pro1041.utils.JdbcHelper;
import pro1041.utils.MsgBox;
import pro1041.utils.Validator;

public class AccountInformationFrame extends javax.swing.JInternalFrame {

    private EmployeeDAO dao = new EmployeeDAO();
    private Employee user = Auth.getUser();

    public AccountInformationFrame() {
        initComponents();
        init();
    }

    private void init() {
        txtID.setText(String.valueOf(user.getId()));
        txtUsername.setText(user.getUsername());
        txtRole.setText(String.valueOf(user.getRole()));
        txtFullname.setText(user.getFullname());
        txtEmail.setText(user.getEmail());
        txtPhoneNumber.setText(user.getPhoneNumber());
        txtAddress.setText(user.getAddress());
        if (user.isGender() == true) {
            rdoMale.setSelected(true);
        } else {
            rdoFemale.setSelected(true);
        }

        if (user.getRole() == Role.MANAGER) {
            txtUsername.setEditable(true);
            txtFullname.setEditable(true);
        }
    }

    private void update() {
        if (Validator.isContainSpecialCharacter(txtUsername, "Tên đăng nhập không được chứa kí tự đặc biệt, vui lòng thử lại !")) {
            return;
        }

        if (JdbcHelper.getListOfArray("SELECT ID FROM EMPLOYEE WHERE USERNAME = ? AND ID != ?", 1, txtUsername.getText(), user.getId()).size() > 0) {
            MsgBox.alert(null, "Tên đăng nhập đã có người sử dụng, vui lòng nhập tên khác !");
            txtUsername.requestFocus();
            return;
        }

        if (JdbcHelper.getListOfArray("SELECT ID FROM EMPLOYEE WHERE EMAIL = ? AND ID != ?", 1, txtEmail.getText(), user.getId()).size() > 0) {
            MsgBox.alert(null, "Email đã có người sử dụng, vui lòng nhập email khác !");
            txtEmail.requestFocus();
            return;
        }

        if (JdbcHelper.getListOfArray("SELECT ID FROM EMPLOYEE WHERE PHONE_NUMBER = ? AND ID != ?", 1, txtPhoneNumber.getText(), user.getId()).size() > 0) {
            MsgBox.alert(null, "Số điện thoại đã có người sử dụng, vui lòng nhập số điện thoại khác !");
            txtPhoneNumber.requestFocus();
            return;
        }

        user.setUsername(txtUsername.getText());
        user.setFullname(txtFullname.getText());
        user.setEmail(txtEmail.getText());
        user.setPhoneNumber(txtPhoneNumber.getText());
        user.setAddress(txtAddress.getText());
        if (rdoMale.isSelected()) {
            user.setGender(true);
        } else {
            user.setGender(false);
        }
        dao.updateUserInfo(user);
        MsgBox.alert(this, "Cập nhật thành công");
        init();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        txtUsername = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtFullname = new javax.swing.JTextField();
        txtPhoneNumber = new javax.swing.JTextField();
        txtAddress = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        rdoMale = new javax.swing.JRadioButton();
        rdoFemale = new javax.swing.JRadioButton();
        btnUpdate = new javax.swing.JButton();
        txtRole = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setTitle("ShoeStoreSys - Thông tin tài khoản");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logoExtraSmall.jpg"))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 153, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("THÔNG TIN TÀI KHOẢN");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel2.setText("ID:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel3.setText("Username:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel4.setText("Role:");

        txtID.setEditable(false);
        txtID.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtID.setMargin(new java.awt.Insets(2, 15, 2, 2));
        txtID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDActionPerformed(evt);
            }
        });

        txtUsername.setEditable(false);
        txtUsername.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtUsername.setMargin(new java.awt.Insets(2, 15, 2, 2));
        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel5.setText("Fullname:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel6.setText("Gender:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel7.setText("Email:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel8.setText("Phone number:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel9.setText("Address:");

        txtFullname.setEditable(false);
        txtFullname.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtFullname.setMargin(new java.awt.Insets(2, 15, 2, 2));
        txtFullname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFullnameActionPerformed(evt);
            }
        });

        txtPhoneNumber.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtPhoneNumber.setMargin(new java.awt.Insets(2, 15, 2, 2));
        txtPhoneNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneNumberActionPerformed(evt);
            }
        });

        txtAddress.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtAddress.setMargin(new java.awt.Insets(2, 15, 2, 2));
        txtAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddressActionPerformed(evt);
            }
        });

        txtEmail.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtEmail.setMargin(new java.awt.Insets(2, 15, 2, 2));
        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdoMale);
        rdoMale.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        rdoMale.setText("Male");
        rdoMale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoMaleActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdoFemale);
        rdoFemale.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        rdoFemale.setText("Female");
        rdoFemale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoFemaleActionPerformed(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/update-user-skin-type-7-40.png"))); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        txtRole.setEditable(false);
        txtRole.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtRole.setMargin(new java.awt.Insets(2, 15, 2, 2));
        txtRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRoleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rdoMale)
                                .addGap(18, 18, 18)
                                .addComponent(rdoFemale))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtPhoneNumber, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtID)
                            .addComponent(txtUsername)
                            .addComponent(txtFullname, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)))
                    .addComponent(txtRole, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(69, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(267, 267, 267))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFullname, txtID, txtUsername});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFullname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdoMale)
                    .addComponent(rdoFemale))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {rdoFemale, rdoMale, txtAddress, txtEmail, txtFullname, txtID, txtRole, txtUsername});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIDActionPerformed

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed

    private void txtFullnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFullnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFullnameActionPerformed

    private void txtPhoneNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneNumberActionPerformed

    private void txtAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddressActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void rdoMaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoMaleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoMaleActionPerformed

    private void rdoFemaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoFemaleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoFemaleActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (Validator.isNull(txtEmail, "Vui lòng nhập email !")
                || Validator.isNull(txtPhoneNumber, "Vui lòng nhập số điện thoại !")
                || Validator.isNull(txtAddress, "Vui lòng nhập địa chỉ !")
                || Validator.isNull(txtUsername, "Vui lòng nhập tên tài khoản !")
                || Validator.isNull(txtFullname, "Vui lòng nhập họ tên !")
                || Validator.isValidEmail(txtEmail, "Vui lòng nhập email đúng định dạng !")
                || Validator.isValidPhone(txtPhoneNumber, "Số điện thoại không hợp lệ")) {
            return;
        }
        update();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void txtRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRoleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRoleActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JRadioButton rdoFemale;
    private javax.swing.JRadioButton rdoMale;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFullname;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtPhoneNumber;
    private javax.swing.JTextField txtRole;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
