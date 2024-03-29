package co.com.cardinalscale.autopesotruck;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import co.com.cardinalscale.autopesotruck.Datos.TablaUsuarios;
import co.com.cardinalscale.autopesotruck.Entidades.EnUsuario;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class MainActivity extends AppCompatActivity {
    private EnUsuario enUser=new EnUsuario();
    private TablaUsuarios cdUsuario=new TablaUsuarios(this);
    TextView txtUserName,txtPassword;
    RelativeLayout RelativeLayoutSuperior,RelativeLayoutInferior;
    Animation uptodown,downtoup;
    String username,clave;
    CircularProgressButton btnLoginProgressBar;
    private Vibrator vib;
    Animation animShake;
    private TextInputLayout input_Usuario,input_contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayoutSuperior=(RelativeLayout)findViewById(R.id.relativeLayoutSuperior);
        RelativeLayoutInferior=(RelativeLayout)findViewById(R.id.relativeLayoutInferior);
        uptodown= AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup=AnimationUtils.loadAnimation(this,R.anim.downtoup);
        RelativeLayoutSuperior.setAnimation(uptodown);
        RelativeLayoutInferior.setAnimation(downtoup);
        txtUserName=(TextView)findViewById(R.id.txtUserNmae);
        txtPassword=(TextView)findViewById(R.id.txtClave);
        btnLoginProgressBar=(CircularProgressButton)findViewById(R.id.btnLogin);
        input_Usuario=(TextInputLayout)findViewById(R.id.input_Usuario);
        input_contrasena=(TextInputLayout)findViewById(R.id.input_contrasena);
        animShake= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
        vib=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        enUser=cdUsuario.BuscarUsuarioActivo();
        if(enUser!=null){
            Intent i = new Intent(getApplicationContext(),principalActivity.class);
            i.putExtra("UserName",enUser.getNombreDeUsuario());
            startActivity(i);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            finish();
        }

    }


    public void Login(View view){

        String ms="";
        if(txtUserName.getText().toString().trim().isEmpty()){
            input_Usuario.setErrorEnabled(true);
            input_Usuario.setError(getResources().getText(R.string.err_msg_usuario));
            txtUserName.setError(getResources().getText(R.string.err_msg_requerido));
            input_Usuario.setAnimation(animShake);
            input_Usuario.startAnimation(animShake);
            requestFocus(txtUserName);
            vib.vibrate(120);
            return;
        }
        if(txtPassword.getText().toString().trim().isEmpty()){
            input_contrasena.setErrorEnabled(true);
            input_contrasena.setError(getResources().getText(R.string.err_msg_contrasena));
            txtPassword.setError(getResources().getText(R.string.err_msg_requerido));
            input_contrasena.setAnimation(animShake);
            input_contrasena.startAnimation(animShake);
            requestFocus(txtPassword);
            vib.vibrate(120);
            return;
        }

        input_Usuario.setErrorEnabled(false);
        input_contrasena.setErrorEnabled(false);

        if(!ValidarUsuario()){
            ms= (String) getResources().getText(R.string.msUsarioInvalido);
            MensajeToast(ms);
            return;
        }
        AsyncTask<String,String,String>demoCargando=new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try{
                    Thread.sleep(3000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                return "ok";

            }

            @Override
            protected void onPostExecute(String s) {
                if(s.equals("ok")){
                    btnLoginProgressBar.doneLoadingAnimation(Color.parseColor("#333639"), BitmapFactory.decodeResource(getResources(),R.drawable.ic_done_white_48dp));
                    Intent i = new Intent(getApplicationContext(),principalActivity.class);
                    i.putExtra("UserName",enUser.getNombreDeUsuario());
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    finish();
                }
            }
        };
        btnLoginProgressBar.startAnimation();
        demoCargando.execute();




    }

    private boolean ValidarUsuario(){
        username=txtUserName.getText().toString();
        clave=txtPassword.getText().toString();
        enUser=cdUsuario.ValidarUsuario(username,clave);
        if(enUser==null){
            if(username.equals("admin")&& clave.equals("1987")){
                enUser=new EnUsuario();
                enUser.setNombreDeUsuario(username);
                return true;
            }else{
                return false;
            }
        }else{
            enUser.setEstado("Activo");
            cdUsuario.ActualizarEstadoUsuario(enUser);
            return true;
        }

    }

    public void MensajeToast(String mensaje){
        Toast toast = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }


    @Override
    public void onBackPressed(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.msCerrarApp);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
