package com.quickbasket.app;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    private static final int INK=Color.rgb(24,35,29), GREEN=Color.rgb(18,129,77), PALE=Color.rgb(245,248,244), MUTED=Color.rgb(111,123,115), LINE=Color.rgb(229,235,228);
    private final List<Product> products=ProductCatalog.all();
    private LocalStore localStore;
    private final Map<Integer,Integer> cart=new LinkedHashMap<>();
    private final ArrayList<String> addresses=new ArrayList<>();
    private final ArrayList<String> orders=new ArrayList<>();
    private LinearLayout root, content; private TextView cartButton, cartTabButton; private String page="Home", category="All"; private String selectedAddress="Home · Add your delivery address";
    @Override public void onCreate(Bundle b){super.onCreate(b);loadState();buildShell();configureSystemBars();showHome();requestNotifPermission();}
    @SuppressWarnings("deprecation")
    @Override public void onBackPressed(){if(!"Home".equals(page)){navigate("Home");}else{super.onBackPressed();}}
    @SuppressWarnings("deprecation")
    private void configureSystemBars(){
        if(Build.VERSION.SDK_INT>=30){
            WindowInsetsController controller=getWindow().getInsetsController();
            if(controller!=null)controller.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS|WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS|WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
        }else{
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().setNavigationBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    private void loadState(){
        localStore=new LocalStore(this);
        LocalStore.Snapshot snapshot=localStore.load();
        cart.putAll(snapshot.cart);
        addresses.addAll(snapshot.addresses);
        orders.addAll(snapshot.orders);
        selectedAddress=snapshot.selectedAddress;
    }
    private void saveState(){localStore.save(cart,addresses,orders,selectedAddress);}
    @SuppressWarnings("deprecation")
    private void buildShell(){root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Color.WHITE);setContentView(root);root.setOnApplyWindowInsetsListener((view,insets)->{if(Build.VERSION.SDK_INT>=30){android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars());view.setPadding(bars.left,bars.top,bars.right,bars.bottom);}else{view.setPadding(insets.getSystemWindowInsetLeft(),insets.getSystemWindowInsetTop(),insets.getSystemWindowInsetRight(),insets.getSystemWindowInsetBottom());}return insets;});LinearLayout top=new LinearLayout(this);top.setPadding(dp(18),dp(12),dp(18),dp(12));top.setGravity(Gravity.CENTER_VERTICAL);top.setBackgroundColor(Color.WHITE);TextView logo=text("quickbasket",22,INK,true);logo.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));top.addView(logo,new LinearLayout.LayoutParams(0,dp(34),1));cartButton=text("🛒  "+cartCount(),15,GREEN,true);cartButton.setOnClickListener(v->navigate("Cart"));top.addView(cartButton);root.addView(top);ScrollView sc=new ScrollView(this);content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);sc.addView(content);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));LinearLayout nav=new LinearLayout(this);nav.setBackgroundColor(Color.WHITE);nav.setElevation(dp(8));String[] tabs={"Home","Orders","Cart","Account"};for(String t:tabs){TextView b=text((t.equals("Home")?"⌂":t.equals("Orders")?"▤":t.equals("Cart")?"🛒":"☻")+"\n"+(t.equals("Cart")?cartTabLabel():t),11,t.equals(page)?GREEN:MUTED,true);b.setGravity(Gravity.CENTER);b.setPadding(0,dp(8),0,dp(7));b.setOnClickListener(v->navigate(t));if(t.equals("Cart"))cartTabButton=b;nav.addView(b,new LinearLayout.LayoutParams(0,dp(58),1));}root.addView(nav);}
    private void navigate(String p){page=p;buildShell();if(p.equals("Home"))showHome();else if(p.equals("Cart"))showCart();else if(p.equals("Orders"))showOrders();else showAccount();}
    private void showHome(){content.removeAllViews();TextView loc=text("⌖  Delivering to",11,MUTED,false);loc.setPadding(dp(18),dp(6),dp(18),0);content.addView(loc);TextView address=text(selectedAddress+"  ▾",14,INK,true);address.setPadding(dp(18),dp(2),dp(18),dp(13));address.setOnClickListener(v->chooseAddress());content.addView(address);EditText search=new EditText(this);search.setSingleLine();search.setTextSize(13);search.setHint("Search for fruits, vegetables and more");search.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_search,0,0,0);search.setPadding(dp(13),0,dp(13),0);search.setBackground(bg(PALE,dp(12),0));LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,dp(46));sp.setMargins(dp(16),0,dp(16),dp(14));content.addView(search,sp);TextView banner=text("  ⚡  Groceries at your door in minutes\n      Fresh picks. Great prices. Every day.",17,Color.WHITE,true);banner.setGravity(Gravity.CENTER_VERTICAL);banner.setBackground(bg(GREEN,dp(16),0));LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,dp(100));bp.setMargins(dp(16),0,dp(16),dp(17));content.addView(banner,bp);TextView title=text("Shop by category",19,INK,true);title.setPadding(dp(18),0,0,dp(8));content.addView(title);HorizontalScrollView hsv=new HorizontalScrollView(this);LinearLayout chips=new LinearLayout(this);String[] cats={"All","Fruits","Vegetables","Dairy","Bakery"};for(String c:cats){TextView chip=text(c,12,category.equals(c)?Color.WHITE:INK,true);chip.setGravity(Gravity.CENTER);chip.setPadding(dp(16),dp(9),dp(16),dp(9));chip.setBackground(bg(category.equals(c)?GREEN:PALE,dp(22),0));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-2,-2);cp.setMargins(dp(6),0,dp(6),0);chips.addView(chip,cp);chip.setOnClickListener(v->{category=c;showHome();});}hsv.addView(chips);content.addView(hsv);TextView featured=text("Popular near you",19,INK,true);featured.setPadding(dp(18),dp(20),0,dp(8));content.addView(featured);LinearLayout grid=new LinearLayout(this);grid.setOrientation(LinearLayout.VERTICAL);for(int row=0;row<(products.size()+1)/2;row++){LinearLayout line=new LinearLayout(this);for(int col=0;col<2;col++){int id=row*2+col;if(id>=products.size()||(!category.equals("All")&&!category.equals(product(id).category)))continue;View card=productCard(id);card.setTag(id);line.addView(card,new LinearLayout.LayoutParams(0,-2,1));}if(line.getChildCount()>0)grid.addView(line);}content.addView(grid);TextView empty=text("No products match your search.",13,MUTED,false);empty.setGravity(Gravity.CENTER);empty.setVisibility(View.GONE);empty.setPadding(dp(16),dp(24),dp(16),dp(24));content.addView(empty);TextView notice=text("✓  Products and your cart are saved on this device for offline access.",11,MUTED,false);notice.setPadding(dp(18),dp(10),dp(18),dp(24));content.addView(notice);search.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int count){String q=s.toString().trim().toLowerCase();int matches=0;for(int row=0;row<grid.getChildCount();row++){LinearLayout line=(LinearLayout)grid.getChildAt(row);for(int col=0;col<line.getChildCount();col++){View card=line.getChildAt(col);int id=(Integer)card.getTag();boolean visible=product(id).name.toLowerCase().contains(q);card.setVisibility(visible?View.VISIBLE:View.GONE);if(visible)matches++;}}empty.setVisibility(matches==0?View.VISIBLE:View.GONE);featured.setText(q.isEmpty()?"Popular near you":"Search results");}public void afterTextChanged(android.text.Editable e){}});}
    private View productCard(int id){
        Product product=product(id);
        LinearLayout card=new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(10),dp(10),dp(10),dp(10));
        card.setBackground(bg(Color.WHITE,dp(14),LINE));
        LinearLayout.LayoutParams margin=new LinearLayout.LayoutParams(-1,-2);
        margin.setMargins(dp(6),dp(5),dp(6),dp(5));
        card.setLayoutParams(margin);
        TextView image=text(product.emoji,46,INK,false);
        image.setGravity(Gravity.CENTER);
        image.setBackground(bg(product.imageColor,dp(12),0));
        card.addView(image,new LinearLayout.LayoutParams(-1,dp(112)));
        TextView delivery=text("⚡  10 min",10,GREEN,true);
        delivery.setPadding(0,dp(8),0,0);
        card.addView(delivery);
        TextView name=text(product.name,13,INK,true);
        name.setPadding(0,dp(4),0,0);
        card.addView(name);
        TextView unit=text(product.unit,11,MUTED,false);
        unit.setPadding(0,dp(3),0,dp(8));
        card.addView(unit);
        LinearLayout bottom=new LinearLayout(this);
        bottom.setGravity(Gravity.CENTER_VERTICAL);
        TextView price=text("₹"+product.price,14,INK,true);
        bottom.addView(price,new LinearLayout.LayoutParams(0,dp(38),1));
        bottom.addView(cartAction(id));
        card.addView(bottom);
        card.setOnClickListener(v->showProduct(id));
        return card;
    }

    private View cartAction(int productId){
        int quantity=cartQuantity(productId);
        if(quantity==0){
            TextView add=text("ADD",12,GREEN,true);
            add.setGravity(Gravity.CENTER);
            add.setMinWidth(dp(58));
            add.setPadding(dp(10),dp(8),dp(10),dp(8));
            add.setBackground(bg(Color.WHITE,dp(8),GREEN));
            add.setOnClickListener(v->adjustCart(productId,1));
            return add;
        }
        LinearLayout stepper=new LinearLayout(this);
        stepper.setGravity(Gravity.CENTER);
        stepper.setBackground(bg(GREEN,dp(8),0));
        TextView minus=stepperButton("−");
        TextView count=stepperButton(String.valueOf(quantity));
        TextView plus=stepperButton("+");
        stepper.addView(minus);stepper.addView(count);stepper.addView(plus);
        minus.setOnClickListener(v->adjustCart(productId,-1));
        plus.setOnClickListener(v->adjustCart(productId,1));
        return stepper;
    }

    private TextView stepperButton(String label){
        TextView button=text(label,14,Color.WHITE,true);
        button.setGravity(Gravity.CENTER);
        button.setMinWidth(dp(30));
        button.setMinHeight(dp(36));
        return button;
    }

    private void adjustCart(int productId,int change){
        int next=cartQuantity(productId)+change;
        if(next<=0)cart.remove(productId);else cart.put(productId,next);
        saveState();
        updateCartCount();
        if(page.equals("Home"))showHome();
        else if(page.equals("Cart"))showCart();
    }
    private void showProduct(int id){page="Product";buildShell();content.removeAllViews();TextView back=text("‹  Back to groceries",14,GREEN,true);back.setPadding(dp(18),dp(10),dp(18),dp(16));back.setOnClickListener(v->navigate("Home"));content.addView(back);TextView image=text(product(id).emoji,86,INK,false);image.setGravity(Gravity.CENTER);image.setBackground(bg(product(id).imageColor,dp(20),0));LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(-1,dp(250));ip.setMargins(dp(16),0,dp(16),dp(20));content.addView(image,ip);TextView delivery=text("⚡  Delivered in 10 minutes",12,GREEN,true);delivery.setPadding(dp(20),0,dp(20),dp(10));content.addView(delivery);TextView title=text(product(id).name,25,INK,true);title.setPadding(dp(20),0,dp(20),dp(7));content.addView(title);TextView meta=text(product(id).unit+" · Fresh from trusted local farms",13,MUTED,false);meta.setPadding(dp(20),0,dp(20),dp(16));content.addView(meta);TextView price=text("₹"+product(id).price+"     ",22,INK,true);price.setPadding(dp(20),0,dp(20),dp(5));content.addView(price);TextView mrp=text("MRP ₹"+product(id).mrp+"  ·  You save ₹"+(product(id).mrp-product(id).price),12,GREEN,true);mrp.setPadding(dp(20),0,dp(20),dp(22));content.addView(mrp);TextView about=text("About this product\n\n"+product(id).description,14,INK,false);about.setPadding(dp(20),dp(18),dp(20),dp(22));about.setBackground(bg(PALE,dp(14),0));LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,-2);ap.setMargins(dp(16),0,dp(16),dp(18));content.addView(about,ap);TextView add=button("Add to cart  ·  ₹"+product(id).price);add.setOnClickListener(v->{cart.put(id,cartQuantity(id)+1);saveState();Toast.makeText(this,product(id).name+" added to cart",Toast.LENGTH_SHORT).show();navigate("Cart");});content.addView(add,buttonParams());}
    private void showCart(){content.removeAllViews();header("Your Cart ("+cartCount()+")",cartCount()==1?"1 item in your cart":""+cartCount()+" items in your cart");if(cart.isEmpty()){emptyState("🛒","Your cart is empty","Add products to continue.","Browse products",()->navigate("Home"));return;}for(Integer id:new ArrayList<>(cart.keySet())){LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(14),dp(10),dp(14),dp(10));row.setBackground(bg(Color.WHITE,dp(14),LINE));LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,dp(86));rp.setMargins(dp(16),dp(5),dp(16),dp(5));content.addView(row,rp);TextView em=text(product(id).emoji,30,INK,false);em.setGravity(Gravity.CENTER);em.setBackground(bg(product(id).imageColor,dp(10),0));row.addView(em,new LinearLayout.LayoutParams(dp(58),dp(58)));TextView label=text(product(id).name+"\n₹"+product(id).price+" each",13,INK,true);label.setPadding(dp(12),0,0,0);row.addView(label,new LinearLayout.LayoutParams(0,-2,1));row.addView(cartAction(id));}
        int total=cartTotal();TextView summary=text("Bill details\n\nItem total     ₹"+total+"\nDelivery fee     "+(total>=299?"FREE":"₹25")+"\nHandling fee     ₹5\n\nTo pay     ₹"+(total+(total>=299?0:25)+5),15,INK,true);summary.setPadding(dp(18),dp(16),dp(18),dp(16));summary.setBackground(bg(PALE,dp(14),0));LinearLayout.LayoutParams sm=new LinearLayout.LayoutParams(-1,-2);sm.setMargins(dp(16),dp(18),dp(16),dp(14));content.addView(summary,sm);TextView checkout=button("Place order  ·  ₹"+(total+(total>=299?0:25)+5));checkout.setOnClickListener(v->placeOrder());content.addView(checkout,buttonParams());}
    private void placeOrder(){if(cart.isEmpty()){new AlertDialog.Builder(this).setTitle("Cart is empty").setMessage("Your cart is empty. Add products to continue.").setPositiveButton("Browse products",(d,w)->navigate("Home")).setNegativeButton("Cancel",null).show();return;}if(selectedAddress.equals("Home · Add your delivery address")){new AlertDialog.Builder(this).setTitle("Add a delivery address").setMessage("Choose an address before placing your order.").setPositiveButton("Add address",(d,w)->addAddress(-1)).setNegativeButton("Cancel",null).show();return;}String id="QB"+(System.currentTimeMillis()%1000000);StringBuilder snapshot=new StringBuilder(id).append('|').append(selectedAddress).append('|');for(Map.Entry<Integer,Integer> e:cart.entrySet())snapshot.append(e.getKey()).append(':').append(e.getValue()).append(',');orders.add(0,snapshot.toString());cart.clear();saveState();updateCartCount();sendNotification("Order confirmed", "Your order "+id+" is being packed.");new AlertDialog.Builder(this).setTitle("Order placed 🎉").setMessage("Order "+id+" is confirmed. Track your delivery in My Orders.").setPositiveButton("Track order",(d,w)->navigate("Orders")).show();}
    private void showOrders(){content.removeAllViews();header("My Orders","Your deliveries, all in one place");if(orders.isEmpty()){emptyState("📦","No orders yet","Your placed orders will appear here.","Start shopping",()->navigate("Home"));return;}for(String saved:orders){String[] parts=saved.split("\\|",3);String id=parts[0];String deliveryAddress=parts.length>2?parts[1]:selectedAddress;String snapshot=parts.length>2?parts[2]:(parts.length>1?parts[1]:"");LinearLayout card=card();TextView t=text("Order "+id+"\n\n🟢  Order confirmed\n      Packing your items\n\nEstimated delivery  ·  10–20 min",14,INK,true);card.addView(t);TextView details=text("View items and tracking  →",12,GREEN,true);details.setPadding(0,dp(12),0,0);card.addView(details);details.setOnClickListener(v->{StringBuilder body=new StringBuilder("Delivery status: Order confirmed\n\nItems in this order:\n");for(String item:snapshot.split(",")){if(item.isEmpty())continue;String[] pair=item.split(":");try{int productId=Integer.parseInt(pair[0]),count=Integer.parseInt(pair[1]);body.append("• ").append(product(productId).name).append(" × ").append(count).append("\n   Status: Packing\n");}catch(Exception ignored){}}body.append("\nDelivering to: ").append(deliveryAddress);new AlertDialog.Builder(this).setTitle("Order "+id).setMessage(body.toString()).setPositiveButton("Done",null).show();});content.addView(card,cardParams());}}
    private void showAccount(){content.removeAllViews();header("Your Account","Manage your QuickBasket profile");LinearLayout c=card();TextView user=text("👤  Darsh\n      QuickBasket member",16,INK,true);c.addView(user);content.addView(c,cardParams());TextView h=text("Saved addresses",18,INK,true);h.setPadding(dp(18),dp(22),0,dp(8));content.addView(h);TextView add=button("＋  Add a new address");add.setOnClickListener(v->addAddress(-1));content.addView(add,buttonParams());for(int i=0;i<addresses.size();i++){final int index=i;LinearLayout row=card();TextView item=text("⌖  "+addresses.get(i)+"\n\nTap to select   ·   Long press to edit",14,INK,true);row.addView(item);item.setOnClickListener(v->{selectedAddress=addresses.get(index);saveState();Toast.makeText(this,"Delivery address selected",Toast.LENGTH_SHORT).show();});item.setOnLongClickListener(v->{addressActions(index);return true;});content.addView(row,cardParams());}TextView hint=text("Tip: tap an address to select it, or press and hold to edit/delete.",11,MUTED,false);hint.setPadding(dp(18),dp(8),dp(18),dp(18));content.addView(hint);TextView promo=button("♧  Try a grocery offer notification");promo.setOnClickListener(v->{sendNotification("QuickBasket Fresh Picks","Fresh deals picked for you. Open the app to shop today's offers.");Toast.makeText(this,"Offer notification sent",Toast.LENGTH_SHORT).show();});content.addView(promo,buttonParams());}
    private void addressActions(int i){new AlertDialog.Builder(this).setTitle("Address options").setItems(new String[]{"Edit address","Delete address"},(d,which)->{if(which==0)addAddress(i);else new AlertDialog.Builder(this).setMessage("Delete this saved address?").setNegativeButton("Cancel",null).setPositiveButton("Delete",(x,y)->{String removed=addresses.remove(i);if(selectedAddress.equals(removed))selectedAddress=addresses.isEmpty()?"Home · Add your delivery address":addresses.get(0);saveState();showAccount();}).show();}).show();}
    private void chooseAddress(){if(addresses.isEmpty()){navigate("Account");return;}String[] opts=addresses.toArray(new String[0]);new AlertDialog.Builder(this).setTitle("Deliver to").setItems(opts,(d,w)->{selectedAddress=opts[w];saveState();showHome();}).setPositiveButton("Manage addresses",(d,w)->navigate("Account")).show();}
    private void addAddress(int edit){EditText input=new EditText(this);input.setHint("Flat, building, street, landmark");input.setText(edit>=0?addresses.get(edit):"");new AlertDialog.Builder(this).setTitle(edit>=0?"Edit address":"Add delivery address").setView(input).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{String value=input.getText().toString().trim();if(value.isEmpty()){Toast.makeText(this,"Enter an address",Toast.LENGTH_SHORT).show();return;}if(edit>=0)addresses.set(edit,value);else addresses.add(value);selectedAddress=value;saveState();showAccount();}).show();}
    private void sendNotification(String title,String body){if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED){Toast.makeText(this,"Allow notifications to see QuickBasket updates.",Toast.LENGTH_LONG).show();return;}NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);Notification.Builder builder;if(Build.VERSION.SDK_INT>=26){nm.createNotificationChannel(new NotificationChannel("quickbasket","QuickBasket updates",NotificationManager.IMPORTANCE_DEFAULT));builder=new Notification.Builder(this,"quickbasket");}else{builder=new Notification.Builder(this).setPriority(Notification.PRIORITY_DEFAULT);}Notification n=builder.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(body).setAutoCancel(true).build();nm.notify((int)System.currentTimeMillis(),n);}
    private void requestNotifPermission(){if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},22);}
    private void header(String title,String subtitle){TextView h=text(title,24,INK,true);h.setPadding(dp(18),dp(14),dp(18),dp(4));content.addView(h);TextView s=text(subtitle,13,MUTED,false);s.setPadding(dp(18),0,dp(18),dp(18));content.addView(s);}
    private void emptyState(String icon,String title,String msg,String action,Runnable run){LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setGravity(Gravity.CENTER);box.setPadding(dp(28),dp(38),dp(28),dp(38));TextView e=text(icon,46,INK,false);e.setGravity(Gravity.CENTER);box.addView(e);TextView t=text(title,18,INK,true);t.setGravity(Gravity.CENTER);t.setPadding(0,dp(14),0,dp(5));box.addView(t);TextView m=text(msg,13,MUTED,false);m.setGravity(Gravity.CENTER);box.addView(m);TextView b=button(action);b.setOnClickListener(v->run.run());box.addView(b,buttonParams());LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(dp(16),dp(25),dp(16),0);content.addView(box,p);}
    private LinearLayout card(){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(16),dp(15),dp(16),dp(15));c.setBackground(bg(Color.WHITE,dp(14),LINE));return c;}
    private LinearLayout.LayoutParams cardParams(){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(dp(16),dp(6),dp(16),dp(6));return p;}
    private TextView button(String label){TextView b=text(label,14,Color.WHITE,true);b.setGravity(Gravity.CENTER);b.setBackground(bg(GREEN,dp(12),0));return b;}
    private LinearLayout.LayoutParams buttonParams(){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(50));p.setMargins(dp(16),dp(6),dp(16),dp(20));return p;}
    private TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    private GradientDrawable bg(int color,int radius,int stroke){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));if(stroke!=0)d.setStroke(dp(1),stroke);return d;}
    private Product product(int id){return products.get(id);}
    private int cartQuantity(int id){Integer quantity=cart.get(id);return quantity==null?0:quantity;}
    private void updateCartCount(){if(cartButton!=null)cartButton.setText("🛒  "+cartCount());if(cartTabButton!=null)cartTabButton.setText("🛒\n"+cartTabLabel());}
    private String cartTabLabel(){int count=cartCount();return count==0?"Cart":"Cart ("+count+")";}
    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}private int cartCount(){int n=0;for(int c:cart.values())n+=c;return n;}private int cartTotal(){int n=0;for(Map.Entry<Integer,Integer> e:cart.entrySet())n+=product(e.getKey()).price*e.getValue();return n;}
}
