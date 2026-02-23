# Script Python para balanço de faturamento
# NOTA: Valores de conserto (ordens de serviço) NÃO são computados no faturamento
# Apenas vendas diretas são incluídas nos totalizadores
import os

def get_billing_summary():
    import json, datetime
    # Usa o diretório do script para encontrar o arquivo JSON
    script_dir = os.path.dirname(os.path.abspath(__file__))
    db_path = os.path.join(script_dir, 'faturamento_db.json')
    
    with open(db_path, 'r', encoding='utf-8') as f:
        db = json.load(f)
    vendas = db.get('vendas', [])
    now = datetime.datetime.now()
    mes_atual = now.month
    ano_atual = now.year
    
    # Calcula APENAS a soma de vendas
    # Ordens de serviço (valorConserto) NÃO são incluídas no faturamento
    total_vendas = sum(
        float(v['valor']) for v in vendas
        if int(v.get('mes', mes_atual)) == mes_atual and int(v.get('ano', ano_atual)) == ano_atual
    )
    total = total_vendas
    return {"total": total, "mes": mes_atual, "ano": ano_atual, "vendas": total_vendas}

if __name__ == "__main__":
    print(get_billing_summary())
