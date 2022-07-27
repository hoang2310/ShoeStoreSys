package pro1041.ui;

import java.util.Random;
import org.apache.commons.mail.EmailException;
import pro1041.dao.EmployeeDAO;
import pro1041.entity.Employee;
import pro1041.utils.Auth;
import pro1041.utils.EmailHelper;
import pro1041.utils.ImageHelper;
import pro1041.utils.MsgBox;
import pro1041.utils.Validator;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class ForgotPasswordDialog extends javax.swing.JDialog {
    
    private EmployeeDAO employeeDAO;
    
    private Employee employee;
    
    private String verifyCode;
    
    public ForgotPasswordDialog(java.awt.Frame parent, boolean modal, EmployeeDAO employeeDAO) {
        super(parent, modal);
        this.employeeDAO = employeeDAO;
        initComponents();
        setIconImage(ImageHelper.getAppIcon());
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnConfirm = new javax.swing.JButton();
        btnReturn = new javax.swing.JButton();
        btnVerify = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNewPassword = new javax.swing.JPasswordField();
        txtConfirmPassword = new javax.swing.JPasswordField();
        txtVerify = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ShoeStoreSys - Quên mật khẩu");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 153, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("QUÊN MẬT KHẨU");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Mã Xác Minh");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Tài Khoản");

        txtUsername.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        btnSearch.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/password-reset-30.png"))); // NOI18N
        btnSearch.setText("Gửi Mã");
        btnSearch.setFocusable(false);
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnConfirm.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/checked-radio-button-40.png"))); // NOI18N
        btnConfirm.setText("Xác Nhận");
        btnConfirm.setEnabled(false);
        btnConfirm.setFocusable(false);
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        btnReturn.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/cancel-40.png"))); // NOI18N
        btnReturn.setText("Trở Lại");
        btnReturn.setFocusable(false);
        btnReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnActionPerformed(evt);
            }
        });

        btnVerify.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnVerify.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/checked-radio-button-40.png"))); // NOI18N
        btnVerify.setText("Xác Minh");
        btnVerify.setEnabled(false);
        btnVerify.setFocusable(false);
        btnVerify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerifyActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Mật Khẩu Mới");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Xác Nhận Mật Khẩu");

        txtNewPassword.setEnabled(false);

        txtConfirmPassword.setEnabled(false);

        txtVerify.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtVerify.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(btnReturn, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtVerify, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnVerify, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(38, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtConfirmPassword, txtNewPassword, txtUsername, txtVerify});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnConfirm, btnReturn});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btnSearch))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(btnVerify)
                    .addComponent(txtVerify, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReturn, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnConfirm, btnReturn, btnSearch, btnVerify, txtConfirmPassword, txtNewPassword, txtUsername, txtVerify});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnReturnActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        if (Validator.isNull(txtUsername, "Vui lòng nhập tên tài khoản !")) {
            return;
        }
        
        String username = txtUsername.getText();
        employee = employeeDAO.selectByUsername(username);
        
        if (employee != null) {
            Random rd = new Random();
            verifyCode = String.format("%06d", rd.nextInt(999999));
            
            try {
                EmailHelper.sendVerifyCode(employee.getEmail(), verifyCode);
            } catch (EmailException ex) {
                MsgBox.alert(null, "Có lỗi xảy ra, hãy thử lại sau");
                return;
            }
            
            txtUsername.setEnabled(false);
            btnSearch.setEnabled(false);
            btnVerify.setEnabled(true);
            txtVerify.setEnabled(true);
            btnConfirm.setEnabled(true);
            
            MsgBox.alert(this, "Đã gửi mã xác minh tới email đăng kí của tài khoản " + employee.getUsername() + "!\nNếu không tìm thấy hãy kiểm tra trong thư mục spam !");
        } else {
            MsgBox.alert(this, "không tìm thấy tài khoản " + username);
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnVerifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerifyActionPerformed
        if (Validator.isNull(txtVerify, "Vui lòng nhập mã xác minh !")) {
            return;
        }
        if (verifyCode.equals(txtVerify.getText())) {
            txtVerify.setEnabled(false);
            btnVerify.setEnabled(false);
            
            txtNewPassword.setEnabled(true);
            txtConfirmPassword.setEnabled(true);
        } else {
            MsgBox.alert(this, "Mã xác minh không chính xác");
        }
    }//GEN-LAST:event_btnVerifyActionPerformed

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        if (Validator.isNull(txtNewPassword, "Vui lòng nhập mật khẩu mới !") || Validator.isNull(txtConfirmPassword, "Vui lòng nhập lại mật khẩu mới !")) {
            return;
        }
        
        String newPassword = new String(txtNewPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        
        if(newPassword.length() < 6){
            MsgBox.alert(this, "Mật khẩu mới phải có ít nhất 6 kí tự");
            txtNewPassword.requestFocus();
            return ;
        }
        
        if (newPassword.equals(confirmPassword)) {
            employee.setPassword(Auth.getSecurePassword(newPassword));
            employeeDAO.updatePassword(employee);
            Auth.setUser(employee);
            MsgBox.alert(this, "Cập nhật mật khẩu mới thành công");
            this.dispose();
        } else {
            MsgBox.alert(this, "Nhập lại mật khẩu mới không chính xác !");
            txtConfirmPassword.requestFocus();
        }
    }//GEN-LAST:event_btnConfirmActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnReturn;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnVerify;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JPasswordField txtNewPassword;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JTextField txtVerify;
    // End of variables declaration//GEN-END:variables

}
