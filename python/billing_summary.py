# Exemplo de script Python para balanço de faturamento
import os

def get_billing_summary():
    import json, datetime
    # Usa o diretório do script para encontrar o arquivo JSON
    script_dir = os.path.dirname(os.path.abspath(__file__))
    db_path = os.path.join(script_dir, 'faturamento_db.json')
    
    with open(db_path, 'r', encoding='utf-8') as f:
        db = json.load(f)
    ordens = db.get('ordens', [])
    vendas = db.get('vendas', [])
    now = datetime.datetime.now()
    mes_atual = now.month
    ano_atual = now.year
    total_ordens = sum(
        float(o['valorConserto']) for o in ordens
        if o.get('status') == 'concluida' and int(o.get('mes', mes_atual)) == mes_atual and int(o.get('ano', ano_atual)) == ano_atual
    )
    total_vendas = sum(
        float(v['valor']) for v in vendas
        if int(v.get('mes', mes_atual)) == mes_atual and int(v.get('ano', ano_atual)) == ano_atual
    )
    total = total_ordens + total_vendas
    return {"total": total, "mes": mes_atual, "ano": ano_atual, "ordens": total_ordens, "vendas": total_vendas}

if __name__ == "__main__":
    print(get_billing_summary())
