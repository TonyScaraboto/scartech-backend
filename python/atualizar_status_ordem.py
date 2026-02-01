import sys, json, datetime

def atualizar_status_ordem(ordem):
    with open('faturamento_db.json', 'r+', encoding='utf-8') as f:
        db = json.load(f)
        ordens = db.get('ordens', [])
        for o in ordens:
            if o.get('id') == ordem.get('id'):
                o['status'] = ordem.get('status')
                o['mes'] = datetime.datetime.now().month
                o['ano'] = datetime.datetime.now().year
        db['ordens'] = ordens
        f.seek(0)
        json.dump(db, f, ensure_ascii=False, indent=2)
        f.truncate()
    return 'Status da ordem atualizado'

if __name__ == "__main__":
    ordem = json.loads(sys.argv[1])
    print(atualizar_status_ordem(ordem))
