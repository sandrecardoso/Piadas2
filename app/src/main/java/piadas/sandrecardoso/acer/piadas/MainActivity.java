package piadas.sandrecardoso.acer.piadas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ShareActionProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import piadas.sandrecardoso.acer.piadas.modelo.Pessoa;

public class MainActivity extends AppCompatActivity {
    private EditText nome;
    private EditText email;
    private ListView listView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Pessoa> listPessoa=new ArrayList<Pessoa>();
    private ArrayAdapter<Pessoa> arrayAdapterPessoa;
    private AdView mAdView;
    private Pessoa pessoaSelecionada;
    ShareActionProvider mShareActionProvider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        nome=(EditText)findViewById(R.id.nome);
        email=(EditText)findViewById(R.id.email);
        listView=(ListView)findViewById(R.id.listView);
        FirebaseCrash.log("Activity created");
        inicialiarfirebase();
        eventoDatabase();

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
               pessoaSelecionada=(Pessoa)parent.getItemAtPosition(position);
               nome.setText(pessoaSelecionada.getNome());
               email.setText(pessoaSelecionada.getEmail());
           }
       });
    }

    private void eventoDatabase() {
        databaseReference.child("Pessoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listPessoa.clear();
                for (DataSnapshot objSnaphot:dataSnapshot.getChildren()){
                    Pessoa p=objSnaphot.getValue(Pessoa.class);
                    listPessoa.add(p);
                }
                arrayAdapterPessoa=new ArrayAdapter <Pessoa>(MainActivity.this,android.R.layout.simple_list_item_1,listPessoa);
                listView.setAdapter(arrayAdapterPessoa);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicialiarfirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();

    }
    @Override
    public boolean onCreateOptionsMenu( Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id=item.getItemId();
       if (id==R.id.novo){
           Pessoa p = new Pessoa();
           p.setUid(UUID.randomUUID().toString());
           p.setNome(nome.getText().toString());
           p.setEmail(email.getText().toString());
           databaseReference.child("Pessoa").child(p.getUid()).setValue(p);
           limparCampos();
       }else if (id==R.id.atualizar){
           Pessoa p=new Pessoa();
           p.setUid(pessoaSelecionada.getUid());
           p.setNome(nome.getText().toString().trim());
           p.setEmail(email.getText().toString().trim());
           databaseReference.child("Pessoa").child(p.getUid()).setValue(p);
           limparCampos();
       } else if (id==R.id.fechar){
           finish();
       } else if (id==R.id.sobre){
           Intent intent=new Intent(MainActivity.this,Sobre.class);
           startActivity(intent);
       }

        return true;
    }

    private void limparCampos() {
        nome.setText("");
        email.setText("");
    }
}
