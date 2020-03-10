# 删除Type=3的标签
POST tb_motor_vehicle_virus/_update_by_query
{
  "query":{
    "bool": {
      "filter": {
        "term": {
          "Type": 3
        }
      }
    }
  },
 "script" : {
   "source": """
		try {
			List Types = ctx._source.Type;
			for (int j = 0; j < Types.length; j++) {
				if (Types[j] == 3) {
					ctx._source.Type.remove(j);
				}
			}
		} catch (Exception e) {}
   """
 }
}
# 将Type!=1&&Type!=2的数据删除
POST tb_motor_vehicle_virus/_delete_by_query
{
  "query": {
    "bool": {
      "must_not": [
        {
          "terms": {
            "Type": [
              1,
              2
            ]
          }
        }
      ]
    }
  }
}
