package pro1041.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JInternalFrame;
import javax.swing.Timer;
import pro1041.utils.Auth;
import pro1041.utils.ImageHelper;
import pro1041.utils.MsgBox;

public class MainFrame extends javax.swing.JFrame {
    
    public MainFrame() {
        initComponents();
        this.setIconImage(ImageHelper.getAppIcon());
        init();
    }
    
    private void addContent(JInternalFrame jInternalFrame) {
        desktopPane.add(jInternalFrame);
        jInternalFrame.setVisible(true);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        desktopPane = new javax.swing.JDesktopPane();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblDongho = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        jToolBar3 = new javax.swing.JToolBar();
        btnProduct = new javax.swing.JButton();
        btnPay = new javax.swing.JButton();
        btnDelivery = new javax.swing.JButton();
        btnEmployee = new javax.swing.JButton();
        btnStatistic = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnLogout = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuHethong = new javax.swing.JMenu();
        mniDangnhap = new javax.swing.JMenuItem();
        mniDangxuat = new javax.swing.JMenuItem();
        mniDoimatkhau = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mniExit = new javax.swing.JMenuItem();
        mnuStatistic = new javax.swing.JMenu();
        mniProduct = new javax.swing.JMenuItem();
        mniPay = new javax.swing.JMenuItem();
        mniDelivery = new javax.swing.JMenuItem();
        mniEmployee = new javax.swing.JMenuItem();
        mniThongke = new javax.swing.JMenuItem();
        mnuTrogiup = new javax.swing.JMenu();
        mniHuongdan = new javax.swing.JMenuItem();
        mniGioithieu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ShoeStoreSys");

        jPanel2.setBackground(new java.awt.Color(253, 204, 6));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logoMain.jpg"))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Viner Hand ITC", 1, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 0, 51));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Sneaker.Beatt");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(95, 95, 95))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 52, Short.MAX_VALUE)
        );

        desktopPane.setBackground(new java.awt.Color(12, 12, 12));
        desktopPane.setPreferredSize(new java.awt.Dimension(1200, 800));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/background.png"))); // NOI18N

        desktopPane.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout desktopPaneLayout = new javax.swing.GroupLayout(desktopPane);
        desktopPane.setLayout(desktopPaneLayout);
        desktopPaneLayout.setHorizontalGroup(
            desktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(desktopPaneLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        desktopPaneLayout.setVerticalGroup(
            desktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        lblDongho.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblDongho.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDongho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Alarm.png"))); // NOI18N
        lblDongho.setText("Ngày giờ");

        lblUser.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/User.png"))); // NOI18N
        lblUser.setText("Status");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDongho, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblDongho, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                .addComponent(lblUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jToolBar3.setBorder(null);
        jToolBar3.setRollover(true);

        btnProduct.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/giay.jpg"))); // NOI18N
        btnProduct.setText("Sản Phẩm");
        btnProduct.setFocusable(false);
        btnProduct.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnProduct.setMargin(new java.awt.Insets(5, 20, 5, 20));
        btnProduct.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductActionPerformed(evt);
            }
        });
        jToolBar3.add(btnProduct);

        btnPay.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnPay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/payment-main-30.png"))); // NOI18N
        btnPay.setText("Thanh Toán");
        btnPay.setFocusable(false);
        btnPay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPay.setMargin(new java.awt.Insets(5, 20, 5, 20));
        btnPay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayActionPerformed(evt);
            }
        });
        jToolBar3.add(btnPay);

        btnDelivery.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnDelivery.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/delivery-main-scooter-30.png"))); // NOI18N
        btnDelivery.setText("Giao Hàng");
        btnDelivery.setFocusable(false);
        btnDelivery.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelivery.setMargin(new java.awt.Insets(5, 20, 5, 20));
        btnDelivery.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDelivery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeliveryActionPerformed(evt);
            }
        });
        jToolBar3.add(btnDelivery);

        btnEmployee.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnEmployee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Clien list.png"))); // NOI18N
        btnEmployee.setText("Nhân Viên");
        btnEmployee.setFocusable(false);
        btnEmployee.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEmployee.setMargin(new java.awt.Insets(5, 20, 5, 20));
        btnEmployee.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeActionPerformed(evt);
            }
        });
        jToolBar3.add(btnEmployee);

        btnStatistic.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnStatistic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Bar chart.png"))); // NOI18N
        btnStatistic.setText("Thống Kê");
        btnStatistic.setFocusable(false);
        btnStatistic.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStatistic.setMargin(new java.awt.Insets(5, 20, 5, 20));
        btnStatistic.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnStatistic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStatisticActionPerformed(evt);
            }
        });
        jToolBar3.add(btnStatistic);
        jToolBar3.add(jSeparator2);

        btnLogout.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logout-icon-32.png"))); // NOI18N
        btnLogout.setText("Đăng Xuất");
        btnLogout.setFocusable(false);
        btnLogout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLogout.setMargin(new java.awt.Insets(5, 20, 5, 20));
        btnLogout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        jToolBar3.add(btnLogout);

        btnExit.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Stop sign.png"))); // NOI18N
        btnExit.setText("Kết Thúc");
        btnExit.setFocusable(false);
        btnExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExit.setMargin(new java.awt.Insets(5, 20, 5, 20));
        btnExit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        jToolBar3.add(btnExit);

        jMenuBar1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        mnuHethong.setText("Hệ Thống");
        mnuHethong.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        mniDangnhap.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        mniDangnhap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Apps-preferences-desktop-user-password-icon-24.png"))); // NOI18N
        mniDangnhap.setText("Thông tin tài khoản");
        mniDangnhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniDangnhapActionPerformed(evt);
            }
        });
        mnuHethong.add(mniDangnhap);

        mniDangxuat.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        mniDangxuat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Exit.png"))); // NOI18N
        mniDangxuat.setText("Đăng xuất");
        mniDangxuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniDangxuatActionPerformed(evt);
            }
        });
        mnuHethong.add(mniDangxuat);

        mniDoimatkhau.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        mniDoimatkhau.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Key.png"))); // NOI18N
        mniDoimatkhau.setText("Đổi Mật khẩu");
        mniDoimatkhau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniDoimatkhauActionPerformed(evt);
            }
        });
        mnuHethong.add(mniDoimatkhau);
        mnuHethong.add(jSeparator1);

        mniExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        mniExit.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        mniExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Stop sign.png"))); // NOI18N
        mniExit.setText("Thoát");
        mniExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniExitActionPerformed(evt);
            }
        });
        mnuHethong.add(mniExit);

        jMenuBar1.add(mnuHethong);

        mnuStatistic.setText("Quản Lý");
        mnuStatistic.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        mniProduct.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        mniProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/giay.jpg"))); // NOI18N
        mniProduct.setText("Sản Phẩm");
        mniProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniProductActionPerformed(evt);
            }
        });
        mnuStatistic.add(mniProduct);

        mniPay.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        mniPay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/money-30.png"))); // NOI18N
        mniPay.setText("Thanh Toán");
        mniPay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPayActionPerformed(evt);
            }
        });
        mnuStatistic.add(mniPay);

        mniDelivery.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        mniDelivery.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/delivery-scooter-30.png"))); // NOI18N
        mniDelivery.setText("Giao Hàng");
        mniDelivery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniDeliveryActionPerformed(evt);
            }
        });
        mnuStatistic.add(mniDelivery);

        mniEmployee.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        mniEmployee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Clien list.png"))); // NOI18N
        mniEmployee.setText("Nhân Viên");
        mniEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniEmployeeActionPerformed(evt);
            }
        });
        mnuStatistic.add(mniEmployee);

        mniThongke.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        mniThongke.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Bar chart.png"))); // NOI18N
        mniThongke.setText("Thống Kê");
        mniThongke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniThongkeActionPerformed(evt);
            }
        });
        mnuStatistic.add(mniThongke);

        jMenuBar1.add(mnuStatistic);

        mnuTrogiup.setText("Trợ Giúp");
        mnuTrogiup.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        mniHuongdan.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        mniHuongdan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Help-icon-16.png"))); // NOI18N
        mniHuongdan.setText("Hướng dẫn sử dụng");
        mniHuongdan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniHuongdanActionPerformed(evt);
            }
        });
        mnuTrogiup.add(mniHuongdan);

        mniGioithieu.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        mniGioithieu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Info.png"))); // NOI18N
        mniGioithieu.setText("Giới thiệu sản phẩm");
        mniGioithieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniGioithieuActionPerformed(evt);
            }
        });
        mnuTrogiup.add(mniGioithieu);

        jMenuBar1.add(mnuTrogiup);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(desktopPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductActionPerformed
        addContent(new ProductManagementFrame());
    }//GEN-LAST:event_btnProductActionPerformed

    private void mniDangnhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniDangnhapActionPerformed
        addContent(new AccountInformationFrame());
    }//GEN-LAST:event_mniDangnhapActionPerformed

    private void btnPayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayActionPerformed
        addContent(new PayFrame());
    }//GEN-LAST:event_btnPayActionPerformed

    private void btnEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeActionPerformed
        addContent(new EmployeeManagementFrame());
    }//GEN-LAST:event_btnEmployeeActionPerformed

    private void btnStatisticActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStatisticActionPerformed
        addContent(new StatisticFrame());
    }//GEN-LAST:event_btnStatisticActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        if (MsgBox.confirm(this, "Bạn có muốn đăng xuất không?")) {
            Auth.clear();
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        if (MsgBox.confirm(this, "Bạn có muốn kết thúc chương trình?")) {
            System.exit(0);
        }
    }//GEN-LAST:event_btnExitActionPerformed

    private void mniDangxuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniDangxuatActionPerformed
        if (MsgBox.confirm(this, "Bạn có muốn đăng xuất không?")) {
            Auth.clear();
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }//GEN-LAST:event_mniDangxuatActionPerformed

    private void mniDoimatkhauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniDoimatkhauActionPerformed
        addContent(new ChangePasswordFrame());
    }//GEN-LAST:event_mniDoimatkhauActionPerformed

    private void btnDeliveryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeliveryActionPerformed
        addContent(new DeliveryManagementFrame());
    }//GEN-LAST:event_btnDeliveryActionPerformed

    private void mniProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniProductActionPerformed
        addContent(new ProductManagementFrame());
    }//GEN-LAST:event_mniProductActionPerformed

    private void mniPayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPayActionPerformed
        addContent(new PayFrame());
    }//GEN-LAST:event_mniPayActionPerformed

    private void mniDeliveryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniDeliveryActionPerformed
        addContent(new DeliveryManagementFrame());
    }//GEN-LAST:event_mniDeliveryActionPerformed

    private void mniEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniEmployeeActionPerformed
        addContent(new EmployeeManagementFrame());
    }//GEN-LAST:event_mniEmployeeActionPerformed

    private void mniExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniExitActionPerformed
        if (MsgBox.confirm(this, "Bạn có muốn kết thúc chương trình?")) {
            System.exit(0);
        }
    }//GEN-LAST:event_mniExitActionPerformed

    private void mniThongkeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniThongkeActionPerformed
        addContent(new StatisticFrame());
    }//GEN-LAST:event_mniThongkeActionPerformed

    private void mniHuongdanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniHuongdanActionPerformed
        addContent(new UserManualFrame());
    }//GEN-LAST:event_mniHuongdanActionPerformed

    private void mniGioithieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniGioithieuActionPerformed
        addContent(new IntroduceFrame());
    }//GEN-LAST:event_mniGioithieuActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelivery;
    private javax.swing.JButton btnEmployee;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPay;
    private javax.swing.JButton btnProduct;
    private javax.swing.JButton btnStatistic;
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JLabel lblDongho;
    private javax.swing.JLabel lblUser;
    private javax.swing.JMenuItem mniDangnhap;
    private javax.swing.JMenuItem mniDangxuat;
    private javax.swing.JMenuItem mniDelivery;
    private javax.swing.JMenuItem mniDoimatkhau;
    private javax.swing.JMenuItem mniEmployee;
    private javax.swing.JMenuItem mniExit;
    private javax.swing.JMenuItem mniGioithieu;
    private javax.swing.JMenuItem mniHuongdan;
    private javax.swing.JMenuItem mniPay;
    private javax.swing.JMenuItem mniProduct;
    private javax.swing.JMenuItem mniThongke;
    private javax.swing.JMenu mnuHethong;
    private javax.swing.JMenu mnuStatistic;
    private javax.swing.JMenu mnuTrogiup;
    // End of variables declaration//GEN-END:variables

    private void init() {
        if (!Auth.isManager()) {
            btnEmployee.setVisible(false);
            btnStatistic.setVisible(false);
            mniEmployee.setVisible(false);
            mnuStatistic.setVisible(false);
        }
        lblUser.setText(Auth.getUser().getRole().toString() + " : " + Auth.getUser().getUsername());
        dongHo();
    }
    
    void ketThuc() {
        if (MsgBox.confirm(this, "Bạn có muốn kết thúc chương trình?")) {
            System.exit(0);
        }
    }
    
    void dongHo() {
        //clock
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a");
        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lblDongho.setText(format.format(new Date()));
            }
        }).start();
    }
    
}
