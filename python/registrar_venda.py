import sys, json, datetime

def registrar_venda(venda):
    with open('faturamento_db.json', 'r+', encoding='utf-8') as f:
        db = json.load(f)
        vendas = db.get('vendas', [])
        venda['mes'] = datetime.datetime.now().month
        venda['ano'] = datetime.datetime.now().year
        vendas.append(venda)
        db['vendas'] = vendas
        f.seek(0)
        json.dump(db, f, ensure_ascii=False, indent=2)
        f.truncate()
    return 'Venda registrada com sucesso'

if __name__ == "__main__":
    venda = json.loads(sys.argv[1])
    print(registrar_venda(venda))
