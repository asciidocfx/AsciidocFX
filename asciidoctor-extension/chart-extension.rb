Asciidoctor::Extensions.register do
  block do
    named :chart
    on_context :literal
    parse_content_as :literal

    process do |parent, target, attrs|
      chart_type = attrs[2]

      create_image_block parent, attrs
    end
  end
end


Asciidoctor::Extensions.register do
  block_macro do
    named :chart

    process do |parent, target, attrs|
      chart_type = attrs[2]

      create_image_block parent, attrs
    end
  end
end